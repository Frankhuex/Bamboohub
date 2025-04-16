package com.huex.bamboohub.service;
import com.huex.bamboohub.converter.BookConverter;
import com.huex.bamboohub.dao.*;
import com.huex.bamboohub.request.BookReq;
import com.huex.bamboohub.util.JwtUtil;
import com.huex.bamboohub.util.RoleUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @InjectMocks private BookServiceImpl bookService;
    @Mock private BookRepo bookRepo;
    @Mock private ParagraphRepo paraRepo;
    @Mock private RoleRepo roleRepo;
    @Mock private BookConverter bookConverter;
    @Mock private JwtUtil jwtUtil;
    @Mock private RoleUtil roleUtil;
    @Mock private RoleService roleService;

    @Test
    public void testAddNewBook() {
        Book mockBook = new Book();
        mockBook.setId(1L);
        when(bookConverter.toDAO(any(BookReq.class))).thenReturn(mockBook);
        when(bookRepo.save(any(Book.class))).thenReturn(mockBook); // 必须返回非 null

        Paragraph mockPara = new Paragraph();
        when(paraRepo.save(any(Paragraph.class))).thenReturn(mockPara);

        User mockUser = new User();
        when(jwtUtil.parseUser(anyString())).thenReturn(mockUser);

        Role mockRole = new Role();
        when(roleService.putRoleWithoutToken(any(), any(),any())).thenReturn(mockRole);

        // 1. 创建 BookReq 对象
        BookReq bookReq=new BookReq(
                "title",
                Book.Scope.ALLREAD
        );

        // 2. 执行测试
        Long bookId=bookService.addNewBook(anyString(), bookReq).getId();

        // 3. 验证结果
        verify(bookConverter, times(1)).toDAO(any(BookReq.class));
        verify(bookRepo, times(2)).save(any(Book.class));
        verify(paraRepo, times(2)).save(any(Paragraph.class));
        verify(roleService, times(1)).putRoleWithoutToken(any(), any(), any());
        verify(jwtUtil, times(1)).parseUser(anyString());
        assertEquals(mockBook.getId(), bookId);
    }

}