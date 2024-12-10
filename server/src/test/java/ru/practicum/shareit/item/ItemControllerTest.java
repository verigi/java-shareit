package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExpandedDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    @DisplayName("Creating item")
    void shouldCreateItem() throws Exception {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        Mockito.when(itemService.saveItem(anyLong(), any(ItemCreateDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item Name"))
                .andExpect(jsonPath("$.description").value("Item Description"));
    }

    @Test
    @DisplayName("Updating item")
    void shouldUpdateItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Updated Name")
                .description("Updated Description")
                .available(true)
                .build();

        Mockito.when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    @DisplayName("Deleting item")
    void shouldDeleteItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item to delete")
                .build();

        Mockito.when(itemService.deleteItem(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item to delete"));
    }

    @Test
    @DisplayName("Getting item by id")
    void shouldReturnFoundItem() throws Exception {
        ItemExpandedDto itemExpandedDto = ItemExpandedDto.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        Mockito.when(itemService.findItem(anyLong())).thenReturn(itemExpandedDto);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item Name"))
                .andExpect(jsonPath("$.description").value("Item Description"));
    }

    @Test
    @DisplayName("Getting items by owner")
    void shouldReturnAllItemsByOwner() throws Exception {
        ItemExpandedDto itemExpandedDto = ItemExpandedDto.builder()
                .id(1L)
                .name("Item Name")
                .build();

        Mockito.when(itemService.findAllByOwner(anyLong()))
                .thenReturn(Collections.singletonList(itemExpandedDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item Name"));
    }

    @Test
    @DisplayName("Searching items")
    void shouldReturnFoundByTextItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .build();

        Mockito.when(itemService.search(any(String.class)))
                .thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item"));
    }

    @Test
    @DisplayName("Adding comment")
    void shouldAddComment() throws Exception {
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("Great item!")
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Perfect item!")
                .build();

        Mockito.when(itemService.addComment(eq(1L), eq(1L), any(CommentCreateDto.class)))
                .thenReturn(commentDto);

        String jsonContent = objectMapper.writeValueAsString(commentCreateDto);
        System.out.println("Request Body: " + jsonContent);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Perfect item!"));
    }
}