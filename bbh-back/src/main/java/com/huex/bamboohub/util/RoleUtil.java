package com.huex.bamboohub.util;
import com.huex.bamboohub.converter.RoleConverter;
import com.huex.bamboohub.converter.UserConverter;
import com.huex.bamboohub.dao.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.huex.bamboohub.dao.Role.RoleType;

import java.util.Optional;

@Component
public class RoleUtil {
    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private RoleConverter roleConverter;



    public RoleType getRoleType(User user, Book book) {
        return roleRepo.findByUserAndBook(user, book)
                .map(Role::getRoleType)
                .orElse(null);
    }

    public RoleType getRoleType(Optional<User> user, Book book) {
        return user.flatMap(u -> roleRepo.findByUserAndBook(u, book))
                .map(Role::getRoleType)
                .orElse(null);
    }

    public boolean isRole(User user, Book book, RoleType roleType) {
        return roleRepo.findByUserAndBook(user, book)
                .map(Role::getRoleType)
                .filter(rt -> rt == roleType)
                .isPresent();
    }

    public boolean isOwner(User user, Book book) {
        return isRole(user, book, RoleType.OWNER);
    }

    public boolean isAdmin(User user, Book book) {
        return isRole(user, book, RoleType.ADMIN);
    }

    public boolean isEditor(User user, Book book) {
        return isRole(user, book, RoleType.EDITOR);
    }

    public boolean isViewer(User user, Book book) {
        return isRole(user, book, RoleType.VIEWER);
    }

    public boolean hasAnyRole(User user, Book book) {
        return roleRepo.existsByUserAndBook(user, book);
    }

    public boolean hasRoleButNot(User user, Book book, RoleType roleType) {
        return roleRepo.findByUserAndBook(user, book)
                .map(Role::getRoleType)
                .filter(rt -> rt != roleType)
                .isPresent();
    }

    public boolean hasRoleCanView(User user, Book book) {
        return hasAnyRole(user, book);
    }

    public boolean hasRoleCanEdit(User user, Book book) {
        return hasRoleButNot(user, book, RoleType.VIEWER);
    }

    public boolean hasRoleCanAdmin(User user, Book book) {
        return isOwner(user,book) || isAdmin(user,book);
    }

    public boolean hasRoleCannotAdmin(User user, Book book) {
        return isEditor(user, book) || isViewer(user, book);
    }



    public RoleType getRoleType(String token, Book book) {
        return getRoleType(jwtUtil.parseUser(token), book);
    }

    public boolean isRole(String token, Book book, RoleType roleType) {
        return jwtUtil.parseUser(token)
                .map(u -> isRole(u, book, roleType))
                .isPresent();
    }

    public boolean isOwner(String token, Book book) {
        return isRole(token, book, RoleType.OWNER);
    }

    public boolean isAdmin(String token, Book book) {
        return isRole(token, book, RoleType.ADMIN);
    }

    public boolean isEditor(String token, Book book) {
        return isRole(token, book, RoleType.EDITOR);
    }

    public boolean isViewer(String token, Book book) {
        return isRole(token, book, RoleType.VIEWER);
    }

    public boolean hasAnyRole(String token, Book book) {
        return jwtUtil.parseUser(token)
                .map(u -> hasAnyRole(u, book))
                .isPresent();
    }

    public boolean hasRoleButNot(String token, Book book, RoleType roleType) {
        return jwtUtil.parseUser(token)
                .map(u -> hasRoleButNot(u, book, roleType))
                .isPresent();
    }

    public boolean hasRoleCanView(String token, Book book) {
        return hasAnyRole(token, book);
    }

    public boolean hasRoleCanEdit(String token, Book book) {
        return hasRoleButNot(token,book, RoleType.VIEWER);
    }

    public boolean hasRoleCanAdmin(String token, Book book) {
        return isOwner(token,book)||isAdmin(token,book);
    }

    public boolean hasRoleCannotAdmin(String token, Book book) {
        return isEditor(token,book) || isViewer(token,book);
    }

    /////////////////////////////////////////
    // Book : User, Book
    public boolean generalCanEditBook(User user, Book book) {
        return hasRoleCanEdit(user,book);
    }

    public boolean generalCanViewBook(User user, Book book) {
        return book.getScope()!=Book.Scope.PRIVATE || hasAnyRole(user,book);
    }

    public boolean generalCanSearchBook(User user, Book book) {
        return book.getScope()!=Book.Scope.PRIVATE || hasAnyRole(user,book);
    }

    // Paragraph : User, Paragraph

    public boolean generalCanEditParagraph(User user, Paragraph paragraph) {
        return generalCanEditParagraph(user,paragraph.getBook());
    }

    public boolean generalCanViewParagraph(User user, Paragraph paragraph) {
        return generalCanViewParagraph(user,paragraph.getBook());
    }

    public boolean generalCanSearchParagraph(User user, Paragraph paragraph) {
        return generalCanSearchParagraph(user,paragraph.getBook());
    }

    // Paragraph : User, Book

    public boolean generalCanEditParagraph(User user, Book book) {
        return book.getScope()==Book.Scope.ALLEDIT || hasRoleCanEdit(user,book);
    }

    public boolean generalCanViewParagraph(User user, Book book) {
        return book.getScope()==Book.Scope.ALLEDIT
                || book.getScope()==Book.Scope.ALLREAD
                || hasAnyRole(user,book);
    }

    public boolean generalCanSearchParagraph(User user, Book book) {
        // Search is the same as view for paragraphs
        return generalCanViewParagraph(user,book);
    }




    /////////////////////////////////////////
    // Book : String, Book
    public boolean generalCanEditBook(String token, Book book) {
        return hasRoleCanEdit(token,book);
    }

    public boolean generalCanViewBook(String token, Book book) {
        return book.getScope()!=Book.Scope.PRIVATE || hasAnyRole(token,book);
    }

    public boolean generalCanSearchBook(String token, Book book) {
        return generalCanViewBook(token,book);
    }


    // Paragraph : String, Paragraph
    public boolean generalCanEditParagraph(String token, Paragraph paragraph) {
        Book book=paragraph.getBook();
        return generalCanEditParagraph(token,book);
    }

    public boolean generalCanViewParagraph(String token, Paragraph paragraph) {
        Book book=paragraph.getBook();
        return generalCanViewParagraph(token,book);
    }

    public boolean generalCanSearchParagraph(String token, Paragraph paragraph) {
        // Search is the same as view for paragraphs
        return generalCanViewParagraph(token,paragraph);
    }

    // Paragraph : String, Book
    public boolean generalCanEditParagraph(String token, Book book) {
        return book.getScope()==Book.Scope.ALLEDIT || hasRoleCanEdit(token,book);
    }

    public boolean generalCanViewParagraph(String token, Book book) {
        return book.getScope()==Book.Scope.ALLEDIT
                || book.getScope()==Book.Scope.ALLREAD
                || hasAnyRole(token,book);
    }

    public boolean generalCanSearchParagraph(String token, Book book) {
        // Search is the same as view for paragraphs
        return generalCanViewParagraph(token,book);
    }








}
