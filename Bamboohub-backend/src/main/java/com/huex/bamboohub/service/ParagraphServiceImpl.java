package com.huex.bamboohub.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.cache.Cache;

import com.huex.bamboohub.dto.*;
import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.request.*;
import com.huex.bamboohub.converter.*;
import com.huex.bamboohub.util.*;

import jakarta.transaction.Transactional;

@Service
public class ParagraphServiceImpl implements ParagraphService {
    @Autowired private ParagraphRepo paraRepo;
    @Autowired private RoleUtil roleUtil;
    @Autowired private BookRepo bookRepo;
    @Autowired private ParagraphConverter paraConverter;
    @Autowired private BookConverter bookConverter;
    @Autowired private CacheManager cacheManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private ParaRoleRepo paraRoleRepo;

    @Override
    public List<ParagraphDTO> getParagraphsByBookId(String token,Long bookId) throws IllegalArgumentException {
        Book book=bookRepo.findById(bookId)
            .orElseThrow(()->new IllegalArgumentException("Book with id "+bookId+" not found."));
        if (book.getScope()==Book.Scope.PRIVATE && !roleUtil.canView(token,book)) {
            throw new IllegalArgumentException("No permission to get paragraphs.");
        }
        return getParagraphsByBookDAO(book);
    }

    //@Override
    public List<ParagraphDTO> getParagraphsByBookTitle(String token,String bookTitle) throws IllegalArgumentException {
        List<Book> books = bookRepo.findByTitle(bookTitle);
        if (books.isEmpty()) {
            throw new IllegalArgumentException("Book with title " + bookTitle + " not found.");
        } else if (books.size() > 1) {
            throw new IllegalArgumentException("More than one book with title " + bookTitle + " found.");
        }
        Book book=books.get(0);
        if (book.getScope()==Book.Scope.PRIVATE && !roleUtil.canView(token,book)) {
            throw new IllegalArgumentException("No permission to get paragraphs.");
        }
        return getParagraphsByBookDAO(book);
    }

    @Override
    @Cacheable(value="paraIdsOfBook",key="'bookId:'+#bookId")
    public List<Long> getParaIdsByBookId(String token,Long bookId) throws IllegalArgumentException {
        Book book=bookRepo.findById(bookId)
            .orElseThrow(()->new IllegalArgumentException("Book with id "+bookId+" not found."));
        if (book.getScope()==Book.Scope.PRIVATE && !roleUtil.canView(token,book)) {
            throw new IllegalArgumentException("No permission to get paragraph ids.");
        }
        return getParaIdsByBookDAO(book);
    }



    @Override
    public ParagraphDTO addNewParagraph(String token, ParagraphReq paraReq) throws IllegalArgumentException {
        // Check if previous paragraph exists
        Long prevParaId=paraReq.getPrevParaId();
        Paragraph prevPara=paraRepo.findById(prevParaId)
            .orElseThrow(()->new IllegalArgumentException("Previous paragraph with id "+paraReq.getPrevParaId()+" not found."));

        // Check if previous paragraph is the end paragraph
        // End paragraph should not have next paragraph
        Long nextParaId=prevPara.getNextParaId();
        if (nextParaId==null) { throw new IllegalArgumentException("Previous paragraph is the end paragraph."); }

        // Check if next paragraph exists
        Paragraph nextPara=paraRepo.findById(nextParaId)
            .orElseThrow(()->new IllegalArgumentException("Next paragraph with id "+nextParaId+" not found."));

        // Check if having permission to add new paragraph
        Book book=prevPara.getBook();
        Long bookId=book.getId();
        if (book.getScope()!=Book.Scope.ALLEDIT && !roleUtil.canEdit(token,book)) {
            throw new IllegalArgumentException("No permission to add new paragraph.");
        }

        // Save new paragraph
        Paragraph para=paraRepo.save(paraConverter.toDAO(paraReq));
        Long thisId=para.getId();

        List<ParagraphDTO> savedParas=connectParas(new Paragraph[]{prevPara,para,nextPara});

        ParaRole paraRole=new ParaRole(
                jwtUtil.parseUser(token),
                para,
                ParaRole.RoleType.CREATOR
        );
        paraRoleRepo.save(paraRole);

        evictCacheParas(new Long[]{prevParaId,thisId,nextParaId});
        evictCacheParaIds(bookId);
        return savedParas.get(1);
    }

    @Override
    public boolean deleteParagraphById(String token,Long id) throws IllegalArgumentException {
        // Check if paragraph exists
        Paragraph paragraph = paraRepo.findById(id)
            .orElseThrow(()->new IllegalArgumentException("Paragraph with id "+id+" not found."));

        // Check if having permission to delete paragraph
        Book book=paragraph.getBook();
        Long bookId=book.getId();
        if (book.getScope()!=Book.Scope.ALLEDIT && !roleUtil.canEdit(token,book)) {
            throw new IllegalArgumentException("No permission to delete paragraph.");
        }

        Long prevParaId=paragraph.getPrevParaId();
        Long nextParaId=paragraph.getNextParaId();

        // Check if paragraph is start or end paragraph
        if (isStartOrEndPara(paragraph)) {
            throw new IllegalArgumentException("Cannot delete the start or end paragraph of a book.");
        }

        // Get previous and next paragraphs
        Paragraph prevParagraph = paraRepo.findById(prevParaId)
                .orElseThrow(()->new IllegalArgumentException("Previous paragraph with id "+prevParaId+" not found."));
        Paragraph nextParagraph = paraRepo.findById(nextParaId)
                .orElseThrow(()->new IllegalArgumentException("Next paragraph with id "+nextParaId+" not found."));

        // Connect previous and next paragraphs
        connectTwoParas(prevParagraph,nextParagraph);

        // Delete paragraph
        paraRepo.delete(paragraph);

        evictCacheParas(new Long[]{id,prevParaId,nextParaId});
        evictCacheParaIds(bookId);
        paraRepo.delete(paragraph);
        return true;
    }

    @Override
    @Cacheable(value = "paragraphs", key = "'para:' + #id", unless = "#result == null")
    public ParagraphDTO getParagraphById(String token, Long id) throws IllegalArgumentException {
        Paragraph paragraph = paraRepo.findById(id)
            .orElseThrow(()->new IllegalArgumentException("Paragraph with id "+id+" not found."));
        if (paragraph.getBook().getScope()==Book.Scope.PRIVATE && !roleUtil.canView(token,paragraph.getBook())) {
            throw new IllegalArgumentException("No permission to get paragraph.");
        }
        return paraConverter.toDTO(paragraph);
    }

    @Override
    @Transactional
    @CachePut(value = "paragraphs", key = "'para:' + #id", unless = "#result == null")
    public ParagraphDTO updateParagraphById(String token, Long id, ParagraphUpdateReq paraUpdReq) throws IllegalArgumentException {
        Paragraph paragraph = paraRepo.findById(id)
            .orElseThrow(()->new IllegalArgumentException("Paragraph with id "+id+" not found."));
        Book book=paragraph.getBook();
        if (book.getScope()!=Book.Scope.ALLEDIT && !roleUtil.canEdit(token,book)) {
            throw new IllegalArgumentException("No permission to update paragraph.");
        }
        String author=paraUpdReq.getAuthor();
        String content=paraUpdReq.getContent();
        if (StringUtils.hasText(author) && !author.equals(paragraph.getAuthor())) {
            paragraph.setAuthor(author);
        }
        if (StringUtils.hasText(content) && !content.equals(paragraph.getContent())) {
            paragraph.setContent(content);
        }
        Paragraph updatedParagraph = paraRepo.save(paragraph);

        ParaRole paraRole=new ParaRole(
                jwtUtil.parseUser(token),
                updatedParagraph,
                ParaRole.RoleType.CONTRIBUTOR
        );

        paraRoleRepo.save(paraRole);
        return paraConverter.toDTO(updatedParagraph);
    }

    @Override
    @CachePut(value = "paragraphs", key = "'para:' + #id", unless = "#result == null")
    public ParagraphDTO moveUpParagraphById(String token, Long id) throws IllegalArgumentException {
        // Check if paragraph exists
        Paragraph paragraph = paraRepo.findById(id)
            .orElseThrow(()->new IllegalArgumentException("Paragraph with id "+id+" not found."));
        Book book=paragraph.getBook();
        Long bookId=book.getId();

        // Check if having permission to move paragraph up
        if (book.getScope()!=Book.Scope.ALLEDIT && !roleUtil.canEdit(token,book)) {
            throw new IllegalArgumentException("No permission to move paragraph up.");
        }

        // Check if paragraph is the start/end paragraph
        if (isStartOrEndPara(paragraph)) {
            throw new IllegalArgumentException("You cannot move the start/end paragraph.");
        }

        // Check if paragraph is already at the top
        if (book.getStartPara().getNextParaId().equals(id)) {
            throw new IllegalArgumentException("Paragraph is already at the top.");
        }

        // Get previous, next, and previous-previous paragraphs
        Long prevId=paragraph.getPrevParaId();
        Long nextId=paragraph.getNextParaId();
        Paragraph prevPara = paraRepo.findById(paragraph.getPrevParaId())
            .orElseThrow(()->new IllegalArgumentException("Previous paragraph with id "+prevId+" not found."));
        Paragraph nextPara = paraRepo.findById(paragraph.getNextParaId())
            .orElseThrow(()->new IllegalArgumentException("Next paragraph with id "+nextId+" not found."));
        Long prevprevId=prevPara.getPrevParaId();
        Paragraph prevprevPara = paraRepo.findById(prevprevId)
            .orElseThrow(()->new IllegalArgumentException("Previous-previous paragraph with id "+prevprevId+" not found."));

        // Connect four paragraphs
        List<ParagraphDTO> savedParas=connectParas(new Paragraph[]{prevprevPara,paragraph,prevPara,nextPara});

        evictCacheParas(new Long[]{prevprevId,prevId,id,nextId});
        evictCacheParaIds(bookId);

        return savedParas.get(1);
    }

    @Override
    @CachePut(value = "paragraphs", key = "'para:' + #id", unless = "#result == null")
    public ParagraphDTO moveDownParagraphById(String token, Long id) throws IllegalArgumentException {
        // Check if paragraph exists
        Paragraph paragraph = paraRepo.findById(id)
                .orElseThrow(()->new IllegalArgumentException("Paragraph with id "+id+" not found."));
        Book book=paragraph.getBook();
        Long bookId=book.getId();

        // Check if having permission to move paragraph down
        if (book.getScope()!=Book.Scope.ALLEDIT && !roleUtil.canEdit(token,book)) {
            throw new IllegalArgumentException("No permission to move paragraph down.");
        }

        // Check if paragraph is the start/end paragraph
        if (isStartOrEndPara(paragraph)) {
            throw new IllegalArgumentException("You cannot move the start/end paragraph.");
        }

        // Check if paragraph is already at the bottom
        if (book.getEndPara().getPrevParaId().equals(id)) {
            throw new IllegalArgumentException("Paragraph is already at the bottom.");
        }

        // Get previous, next, and next-next paragraphs
        Long prevId=paragraph.getPrevParaId();
        Long nextId=paragraph.getNextParaId();
        Paragraph prevPara = paraRepo.findById(paragraph.getPrevParaId())
                .orElseThrow(()->new IllegalArgumentException("Previous paragraph with id "+prevId+" not found."));
        Paragraph nextPara = paraRepo.findById(paragraph.getNextParaId())
                .orElseThrow(()->new IllegalArgumentException("Next paragraph with id "+nextId+" not found."));
        Long nextnextId=nextPara.getNextParaId();
        Paragraph nextnextPara = paraRepo.findById(nextnextId)
                .orElseThrow(()->new IllegalArgumentException("Next-next paragraph with id "+nextnextId+" not found."));

        // Connect four paragraphs
        List<ParagraphDTO> savedParas=connectParas(new Paragraph[]{prevPara,nextPara,paragraph,nextnextPara});

        evictCacheParas(new Long[]{prevId,id,nextId,nextnextId});
        evictCacheParaIds(bookId);

        return savedParas.get(2);
    }

    @Override
    public BookDTO getBookByParaId(String token, Long paraId) throws IllegalArgumentException {
        Paragraph paragraph = paraRepo.findById(paraId)
            .orElseThrow(() -> new IllegalArgumentException("Paragraph with id "+paraId+" not found."));
        Book book = paragraph.getBook();
        if (!roleUtil.canView(token,book)) {
            throw new IllegalArgumentException("No permission to get book.");
        }
        return bookConverter.toDTO(book);
    }

    @Override
    public List<ParagraphDTO> getAllParagraphs(String token) {
        List<Paragraph> paragraphs = paraRepo.findAll();
        return paraConverter.toDTOs(paragraphs);
    }

    @Override
    public List<ParagraphDTO> searchParagraphsByAny(String query) {
        List<Paragraph> paras=paraRepo.findByAuthorContainingOrContentContaining(query,query);
        List<Paragraph> filteredParas=new ArrayList<>();
        for (Paragraph para : paras) {
            if (para.getBook().getScope()!=Book.Scope.PRIVATE) {
                filteredParas.add(para);
            }
        }
        return paraConverter.toDTOs(filteredParas);
    }

    @Override
    public List<ParagraphDTO> searchParagraphsByAny(String token, String query) {
        List<Paragraph> paras=paraRepo.findByAuthorContainingOrContentContaining(query,query);
        List<Paragraph> filteredParas=new ArrayList<>();
        for (Paragraph para : paras) {
            if (para.getBook().getScope()!=Book.Scope.PRIVATE || roleUtil.canView(token,para.getBook())) {
                filteredParas.add(para);
            }
        }
        return paraConverter.toDTOs(filteredParas);
    }




    private List<ParagraphDTO> getParagraphsByBookDAO(Book book) throws IllegalArgumentException {
        LinkedList<Paragraph> paragraphs=new LinkedList<>();
        paragraphs.add(book.getStartPara());
        while (paragraphs.getLast().getNextParaId()!=null) {
            paragraphs.add(paraRepo.findById(paragraphs.getLast().getNextParaId())
                .orElseThrow(()->new IllegalArgumentException("Next paragraph with id "+paragraphs.getLast().getNextParaId()+" not found.")));
        }
        return paraConverter.toDTOs(paragraphs);
    }

    private List<Long> getParaIdsByBookDAO(Book book) throws IllegalArgumentException {
        System.out.println("getParaIdsByBookDAO"+book.getId());
        LinkedList<Long> paraIds=new LinkedList<>();
        paraIds.add(book.getStartPara().getId());
        while (true) {
            Paragraph paragraph = paraRepo.findById(paraIds.getLast())
                .orElseThrow(()->new IllegalArgumentException("Paragraph with id "+paraIds.getLast()+" not found."));
            if (paragraph.getNextParaId()==null) break;
            paraIds.add(paragraph.getNextParaId());
        }
        return paraIds;
    }

    private List<ParagraphDTO> connectTwoParas(Paragraph para1, Paragraph para2) {
        para1.setNextParaId(para2.getId());
        para2.setPrevParaId(para1.getId());
        Paragraph savedPara1 = paraRepo.save(para1);
        Paragraph savedPara2 = paraRepo.save(para2);
        return paraConverter.toDTOs(new Paragraph[]{savedPara1,savedPara2});
    }


    private List<ParagraphDTO> connectParas(Paragraph[] paras) {
        List<ParagraphDTO> savedParas=new ArrayList<>();
        List<ParagraphDTO> savedPairs=new ArrayList<>();
        for (int i=0; i<paras.length-1; i++) {
            savedPairs=connectTwoParas(paras[i],paras[i+1]);
            savedParas.add(savedPairs.get(0));
        }
        if (savedPairs.size()==2) {
            savedParas.add(savedPairs.get(1));
        }
        return savedParas;
    }

    private boolean isStartOrEndPara(Paragraph para) {
        return para.getPrevParaId()==null
                || para.getNextParaId()==null
                || para.getBook().getStartPara().getId().equals(para.getId())
                || para.getBook().getEndPara().getId().equals(para.getId());
    }

    private void evictCacheParas(Long[] paraIds) {
        Cache cacheParas = cacheManager.getCache("paragraphs");
        if (cacheParas != null){
            for (Long id : paraIds) {
                if (id!=null) {
                    cacheParas.evict("para:"+id);
                }
            }
        }
    }

    private void evictCacheParaIds(Long bookId) {
        Cache cacheParaIds = cacheManager.getCache("paraIdsOfBook");
        if (cacheParaIds != null) {
            cacheParaIds.evict("bookId:"+bookId);
        }
    }
}
