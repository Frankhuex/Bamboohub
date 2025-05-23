package com.huex.bamboohub.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
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
    @Autowired private CacheUtil cacheUtil;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private ParaRoleRepo paraRoleRepo;
    @Autowired private RoleRepo roleRepo;
    @Autowired private UserRepo userRepo;

    @Override
    @Cacheable(value="parasOfBook",key="'bookId:'+#bookId")
    public List<ParagraphDTO> getParagraphsByBookId(String token,Long bookId) throws IllegalArgumentException {
        Book book=bookRepo.findById(bookId)
            .orElseThrow(()->new IllegalArgumentException("Book with id "+bookId+" not found."));
        if (!roleUtil.generalCanViewParagraph(token,book)) {
            throw new IllegalArgumentException("No permission to get paragraphs.");
        }
        return getParagraphsByBookDAO(book);
    }

    @Override
    @Cacheable(value="paraIdsOfBook",key="'bookId:'+#bookId")
    public List<Long> getParaIdsByBookId(String token,Long bookId) throws IllegalArgumentException {
        Book book=bookRepo.findById(bookId)
            .orElseThrow(()->new IllegalArgumentException("Book with id "+bookId+" not found."));
        if (book.getScope()==Book.Scope.PRIVATE && !roleUtil.hasRoleCanView(token,book)) {
            throw new IllegalArgumentException("No permission to get paragraph ids.");
        }
        return getParaIdsByBookDAO(book);
    }



    @Override
    public ParagraphDTO addNewParagraph(String token, ParagraphReq paraReq) throws IllegalArgumentException {
        User user=jwtUtil.parseUser(token).orElse(null);

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
        if (!roleUtil.generalCanEditParagraph(user,book)) {
            throw new IllegalArgumentException("No permission to add new paragraph.");
        }

        // Save new paragraph
        Paragraph para=paraRepo.save(paraConverter.toDAO(paraReq));
        Long thisId=para.getId();

        List<ParagraphDTO> savedParas=connectParas(new Paragraph[]{prevPara,para,nextPara});
        
        if (user!=null) {
            ParaRole paraRole = new ParaRole(
                    user,
                    para,
                    ParaRole.RoleType.CREATOR
            );
            paraRoleRepo.save(paraRole);
        }

        evictCacheParas(new Long[]{prevParaId,thisId,nextParaId});
        evictCacheParasOfBook(bookId);
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
        if (!roleUtil.generalCanEditParagraph(token,book)) {
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
        evictCacheParasOfBook(bookId);
        cacheUtil.clearCache("rolesOfPara","paraId:"+id);

        paraRepo.delete(paragraph);
        return true;
    }

    @Override
    @Cacheable(value = "paragraphs", key = "'paraId:' + #id", unless = "#result == null")
    public ParagraphDTO getParagraphById(String token, Long id) throws IllegalArgumentException {
        Paragraph paragraph = paraRepo.findById(id)
            .orElseThrow(()->new IllegalArgumentException("Paragraph with id "+id+" not found."));
        if (!roleUtil.generalCanViewParagraph(token,paragraph)) {
            throw new IllegalArgumentException("No permission to get paragraph.");
        }
        return paraConverter.toDTO(paragraph);
    }

    @Override
    @Transactional
    @CacheEvict(value = "paragraphs", key = "'paraId:' + #id")
    public ParagraphDTO updateParagraphById(String token, Long id, ParagraphUpdateReq paraUpdReq) throws IllegalArgumentException {
        Paragraph paragraph = paraRepo.findById(id)
            .orElseThrow(()->new IllegalArgumentException("Paragraph with id "+id+" not found."));
        Book book=paragraph.getBook();

        User user=jwtUtil.parseUser(token).orElse(null);
        if (!roleUtil.generalCanEditParagraph(user,book)) {
            throw new IllegalArgumentException("No permission to update paragraph.");
        }
        String author=paraUpdReq.getAuthor();
        String content=paraUpdReq.getContent();
        if (!author.equals(paragraph.getAuthor())) {
            paragraph.setAuthor(author);
        }
        if (!content.equals(paragraph.getContent())) {
            paragraph.setContent(content);
        }
        Paragraph updatedParagraph = paraRepo.save(paragraph);
        



        if (user!=null && !paraRoleRepo.existsByUserAndParagraphAndRoleType(user,updatedParagraph,ParaRole.RoleType.CONTRIBUTOR)) {
            ParaRole paraRole = new ParaRole(
                    user,
                    updatedParagraph,
                    ParaRole.RoleType.CONTRIBUTOR
            );

            paraRoleRepo.save(paraRole);
            cacheUtil.clearCache("rolesOfPara","paraId:"+id);
        }

        evictCacheParasOfBook(book.getId());

        return paraConverter.toDTO(updatedParagraph);
    }

    @Override
    public ParagraphDTO moveUpParagraphById(String token, Long id) throws IllegalArgumentException {
        // Check if paragraph exists
        Paragraph paragraph = paraRepo.findById(id)
            .orElseThrow(()->new IllegalArgumentException("Paragraph with id "+id+" not found."));
        Book book=paragraph.getBook();
        Long bookId=book.getId();

        // Check if having permission to move paragraph up
        if (!roleUtil.generalCanEditParagraph(token,book)) {
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
        evictCacheParasOfBook(bookId);

        return savedParas.get(1);
    }

    @Override
    public ParagraphDTO moveDownParagraphById(String token, Long id) throws IllegalArgumentException {
        // Check if paragraph exists
        Paragraph paragraph = paraRepo.findById(id)
                .orElseThrow(()->new IllegalArgumentException("Paragraph with id "+id+" not found."));
        Book book=paragraph.getBook();
        Long bookId=book.getId();

        // Check if having permission to move paragraph down
        if (!roleUtil.generalCanEditParagraph(token,book)) {
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
        evictCacheParasOfBook(bookId);

        return savedParas.get(2);
    }

    @Override
    public BookDTO getBookByParaId(String token, Long paraId) throws IllegalArgumentException {
        Paragraph paragraph = paraRepo.findById(paraId)
            .orElseThrow(() -> new IllegalArgumentException("Paragraph with id "+paraId+" not found."));
        Book book = paragraph.getBook();
        if (!roleUtil.generalCanViewParagraph(token,book)){
            throw new IllegalArgumentException("No permission to get book.");
        }
        return bookConverter.toDTO(book);
    }

    @Override
    // Secret
    public List<ParagraphDTO> getAllParagraphs(String token) {
        List<Paragraph> paragraphs = paraRepo.findAll();
        return paraConverter.toDTOs(paragraphs);
    }


    @Override
    public ParaSearchDTO searchParagraphsByAny(String token, String query) {
        if (StringUtils.isEmpty(query)) {
            return new ParaSearchDTO(new ArrayList<>());
        }
        User user=jwtUtil.parseUser(token).orElse(null);
//        List<Paragraph> paras=paraRepo.findComplexParagraphs(query,query,null,null);
        List<Paragraph> paras=paraRepo.findByAuthorContainingOrContentContaining(query,query);
        paras=paras.stream().filter(p->p.getPrevParaId()!=null && p.getNextParaId()!=null).toList();
        Map<Long,List<Paragraph>> bookParaMap=new HashMap<>();
        for (Paragraph para : paras) {
//            bookParaMap.getOrDefault(para.getBook().getId(),new ArrayList<>()).add(para);
            if (!bookParaMap.containsKey(para.getBook().getId())) {
                bookParaMap.put(para.getBook().getId(),new ArrayList<>());
            }
            bookParaMap.get(para.getBook().getId()).add(para);
        }
        List<ParaSearchItem> items=new ArrayList<>();
        for (List<Paragraph> paraList : bookParaMap.values()) {
            Book book=paraList.get(0).getBook();
            BookDTOWithRole bookDTOWithRole;
            if (user==null) {
                bookDTOWithRole=bookConverter.toDTOWithRole(book, null);
            }
            else {
                Role role=roleRepo.findByUserAndBook(user, book).orElse(null);
                bookDTOWithRole=bookConverter.toDTOWithRole(book,role);
            }

            if (roleUtil.generalCanSearchParagraph(user,book)) {
                paraList.sort(Comparator.comparing(Paragraph::getCreateTime).reversed());
                ParaSearchItem item = new ParaSearchItem(
                        bookDTOWithRole,
                        paraConverter.toDTOs(paraList)
                );
                items.add(item);
            }
        }
        items.sort((a,b)->b.getParagraphDTOs().size()-a.getParagraphDTOs().size());
        return new ParaSearchDTO(items);
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
        List<String> keys=new ArrayList<>();
        for (Long id : paraIds) {
            if (id!=null) {
                keys.add("paraId:"+id);
            }
        }
        cacheUtil.clearCache("paragraphs",keys);
        cacheUtil.clearCache("rolesOfPara",keys);
    }

    private void evictCacheParasOfBook(Long bookId) {
        cacheUtil.clearCache(List.of("paraIdsOfBook","parasOfBook"),"bookId:"+bookId);
    }
}
