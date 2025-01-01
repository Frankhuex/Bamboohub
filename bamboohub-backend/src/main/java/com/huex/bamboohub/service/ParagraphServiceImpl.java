package com.huex.bamboohub.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    

    @Override
    public List<ParagraphDTO> getParagraphsByBookId(String token,Long bookId) throws IllegalArgumentException {
        Book book=bookRepo.findById(bookId)
            .orElseThrow(()->new IllegalArgumentException("Book with id "+bookId+" not found."));
        if (!book.getIsPublic() && !roleUtil.canView(token,bookId)) {
            throw new IllegalArgumentException("No permission to get paragraphs.");
        }
        return getParagraphsByBookDAO(book);
    }

    @Override
    public List<ParagraphDTO> getParagraphsByBookTitle(String token,String bookTitle) throws IllegalArgumentException {
        List<Book> books = bookRepo.findByTitle(bookTitle);
        if (books.isEmpty()) {
            throw new IllegalArgumentException("Book with title " + bookTitle + " not found.");
        } else if (books.size() > 1) {
            throw new IllegalArgumentException("More than one book with title " + bookTitle + " found.");
        }
        Book book=books.get(0);
        if (!book.getIsPublic() && !roleUtil.canView(token,book.getId())) {
            throw new IllegalArgumentException("No permission to get paragraphs.");
        }
        return getParagraphsByBookDAO(book);
    }

    @Override
    public List<Long> getParaIdsByBookId(String token,Long bookId) throws IllegalArgumentException {
        Book book=bookRepo.findById(bookId)
            .orElseThrow(()->new IllegalArgumentException("Book with id "+bookId+" not found."));
        if (!book.getIsPublic() && !roleUtil.canView(token,bookId)) {
            throw new IllegalArgumentException("No permission to get paragraph ids.");
        }
        return getParaIdsByBookDAO(book);
    }



    @Override
    public Long addNewParagraph(String token,ParagraphRequest paraReq) throws IllegalArgumentException {
        Paragraph prevPara=paraRepo.findById(paraReq.getPrevParaId())
            .orElseThrow(()->new IllegalArgumentException("Previous paragraph with id "+paraReq.getPrevParaId()+" not found."));
        
        if (!roleUtil.canEdit(token,prevPara.getBook().getId())) {
            throw new IllegalArgumentException("No permission to add new paragraph.");
        }
        
        Paragraph para=paraRepo.save(paraConverter.toDAO(paraReq));
        
        Long thisId=para.getId();
        Long nextId=prevPara.getNextParaId();

        prevPara.setNextParaId(thisId);
        paraRepo.save(prevPara);

        para.setNextParaId(nextId);
        paraRepo.save(para);
        try {
            Paragraph nextPara=paraRepo.findById(nextId)
                .orElseThrow(()->new Exception(""));
            nextPara.setPrevParaId(thisId);
            paraRepo.save(nextPara);  
        } catch (Exception e) {}

        return para.getId();
    }

    @Override
    public void deleteParagraphById(String token,Long id) throws IllegalArgumentException {
        Paragraph paragraph = paraRepo.findById(id)
            .orElseThrow(()->new IllegalArgumentException("Paragraph with id "+id+" not found."));

        if (!roleUtil.canEdit(token,paragraph.getBook().getId())) {
            throw new IllegalArgumentException("No permission to delete paragraph.");
        }
        
        if (paragraph.getBook().getStartPara().getId().equals(id)) {
            throw new IllegalArgumentException("Cannot delete the start paragraph of a book.");
        }
        if (paragraph.getPrevParaId()!=null) {
            Paragraph prevParagraph = paraRepo.findById(paragraph.getPrevParaId())
                .orElseThrow(()->new IllegalArgumentException("Previous paragraph with id "+paragraph.getPrevParaId()+" not found."));
            prevParagraph.setNextParaId(paragraph.getNextParaId());
            paraRepo.save(prevParagraph);
        }
        if (paragraph.getNextParaId()!=null) {
            Paragraph nextParagraph = paraRepo.findById(paragraph.getNextParaId())
                .orElseThrow(()->new IllegalArgumentException("Next paragraph with id "+paragraph.getNextParaId()+" not found."));
            nextParagraph.setPrevParaId(paragraph.getPrevParaId());
            paraRepo.save(nextParagraph);
        }
        paraRepo.delete(paragraph);
    }

    @Override
    public ParagraphDTO getParagraphById(String token, Long id) throws IllegalArgumentException {
        Paragraph paragraph = paraRepo.findById(id)
            .orElseThrow(()->new IllegalArgumentException("Paragraph with id "+id+" not found."));
        if (!paragraph.getBook().getIsPublic() && !roleUtil.canView(token,paragraph.getBook().getId())) {
            throw new IllegalArgumentException("No permission to get paragraph.");
        }
        return paraConverter.toDTO(paragraph);
    }

    @Override
    @Transactional
    public ParagraphDTO updateParagraphById(String token, Long id,ParagraphUpdateRequest paraUpdReq) throws IllegalArgumentException {
        Paragraph paragraph = paraRepo.findById(id)
            .orElseThrow(()->new IllegalArgumentException("Paragraph with id "+id+" not found."));
        if (!roleUtil.canEdit(token,paragraph.getBook().getId())) {
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
        return paraConverter.toDTO(updatedParagraph);
    }

    @Override
    public ParagraphDTO moveUpParagraphById(String token, Long id) throws IllegalArgumentException {
        Paragraph paragraph = paraRepo.findById(id)
            .orElseThrow(()->new IllegalArgumentException("Paragraph with id "+id+" not found."));
        Book book=paragraph.getBook();

        if (!roleUtil.canEdit(token,book.getId())) {
            throw new IllegalArgumentException("No permission to move paragraph up.");
        }

        if (book.getStartPara().getId().equals(id)) {
            throw new IllegalArgumentException("You cannot move the start paragraph.");
        }
        if (book.getStartPara().getNextParaId().equals(id)) {
            throw new IllegalArgumentException("Paragraph is already at the top.");
        }
        
        Paragraph prevParagraph = paraRepo.findById(paragraph.getPrevParaId())
            .orElseThrow(()->new IllegalArgumentException("Previous paragraph with id "+paragraph.getPrevParaId()+" not found."));
        
        Long prevprevId=prevParagraph.getPrevParaId();
        Long prevId=paragraph.getPrevParaId();
        Long nextId=paragraph.getNextParaId();

        prevParagraph.setPrevParaId(id);
        prevParagraph.setNextParaId(nextId);

        paragraph.setPrevParaId(prevprevId);
        paragraph.setNextParaId(prevId);

        paraRepo.save(prevParagraph);
        paraRepo.save(paragraph);

        if (nextId!=null) {
            Paragraph nextParagraph = paraRepo.findById(nextId)
                .orElseThrow(()->new IllegalArgumentException("Next paragraph with id "+nextId+" not found."));
            nextParagraph.setPrevParaId(prevId);
            paraRepo.save(nextParagraph);
        }

        if (prevprevId!=null) {
            Paragraph prevprevParagraph = paraRepo.findById(prevprevId)
                .orElseThrow(()->new IllegalArgumentException("Previous paragraph with id "+prevprevId+" not found."));
            prevprevParagraph.setNextParaId(id);
            paraRepo.save(prevprevParagraph);
        }
        return paraConverter.toDTO(paragraph);
    }

    @Override
    public ParagraphDTO moveDownParagraphById(String token, Long id) throws IllegalArgumentException {
        Paragraph paragraph = paraRepo.findById(id)
            .orElseThrow(()->new IllegalArgumentException("Paragraph with id "+id+" not found."));
        Book book=paragraph.getBook();

        if (!roleUtil.canEdit(token,book.getId())) {
            throw new IllegalArgumentException("No permission to move paragraph down.");
        }

        if (book.getStartPara().getId().equals(id)) {
            throw new IllegalArgumentException("You cannot move the start paragraph.");
        }
        if (paragraph.getNextParaId()==null) {
            throw new IllegalArgumentException("Paragraph is already at the bottom.");
        }
        Paragraph nextParagraph = paraRepo.findById(paragraph.getNextParaId())
            .orElseThrow(()->new IllegalArgumentException("Next paragraph with id "+paragraph.getNextParaId()+" not found."));
        
        Long prevId=paragraph.getPrevParaId();
        Long nextId=paragraph.getNextParaId();
        Long nextnextId=nextParagraph.getNextParaId();

        nextParagraph.setNextParaId(id);
        nextParagraph.setPrevParaId(prevId);

        paragraph.setNextParaId(nextnextId);
        paragraph.setPrevParaId(nextId);

        paraRepo.save(nextParagraph);
        paraRepo.save(paragraph);

        if (prevId!=null) {
            Paragraph prevParagraph = paraRepo.findById(prevId)
                .orElseThrow(()->new IllegalArgumentException("Previous paragraph with id "+prevId+" not found."));
            prevParagraph.setNextParaId(nextId);
            paraRepo.save(prevParagraph);
        }

        if (nextnextId!=null) {
            Paragraph nextnextParagraph = paraRepo.findById(nextnextId)
                .orElseThrow(()->new IllegalArgumentException("Next paragraph with id "+nextnextId+" not found."));
            nextnextParagraph.setPrevParaId(id);
            paraRepo.save(nextnextParagraph);
        }
        return paraConverter.toDTO(paragraph);
    }

    @Override
    public BookDTO getBookByParaId(String token, Long paraId) throws IllegalArgumentException {
        Paragraph paragraph = paraRepo.findById(paraId)
            .orElseThrow(() -> new IllegalArgumentException("Paragraph with id "+paraId+" not found."));
        Book book = paragraph.getBook();
        if (!roleUtil.canView(token,book.getId())) {
            throw new IllegalArgumentException("No permission to get book.");
        }
        return bookConverter.toDTO(book); 
    }

    @Override
    public List<ParagraphDTO> getAllParagraphs(String token) {
        List<Paragraph> paragraphs = paraRepo.findAll();
        return paraConverter.toDTOs(paragraphs);
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
        LinkedList<Long> paraIds=new LinkedList<>();
        paraIds.add(book.getStartPara().getId());
        do {
            Paragraph paragraph = paraRepo.findById(paraIds.getLast())
                .orElseThrow(()->new IllegalArgumentException("Paragraph with id "+paraIds.getLast()+" not found."));
            if (paragraph.getNextParaId()!=null) {
                paraIds.add(paragraph.getNextParaId());
            }
        } while (paraIds.getLast()!=null);
        return paraIds;
    }
}
