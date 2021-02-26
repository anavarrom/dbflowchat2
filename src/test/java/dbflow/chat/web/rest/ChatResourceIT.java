package dbflow.chat.web.rest;

import dbflow.chat.Dbflowchat2App;
import dbflow.chat.config.TestSecurityConfiguration;
import dbflow.chat.domain.Chat;
import dbflow.chat.repository.ChatRepository;
import dbflow.chat.repository.search.ChatSearchRepository;
import dbflow.chat.service.ChatService;
import dbflow.chat.service.dto.ChatDTO;
import dbflow.chat.service.mapper.ChatMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dbflow.chat.domain.enumeration.ChatType;
/**
 * Integration tests for the {@link ChatResource} REST controller.
 */
@SpringBootTest(classes = { Dbflowchat2App.class, TestSecurityConfiguration.class })
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class ChatResourceIT {

    private static final String DEFAULT_OWNER = "AAAAAAAAAA";
    private static final String UPDATED_OWNER = "BBBBBBBBBB";

    private static final String DEFAULT_TO = "AAAAAAAAAA";
    private static final String UPDATED_TO = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_LAST_MESSAGE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_MESSAGE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final ChatType DEFAULT_TYPE = ChatType.NOTIFICATION;
    private static final ChatType UPDATED_TYPE = ChatType.NOTIFICATION;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private ChatService chatService;

    /**
     * This repository is mocked in the dbflow.chat.repository.search test package.
     *
     * @see dbflow.chat.repository.search.ChatSearchRepositoryMockConfiguration
     */
    @Autowired
    private ChatSearchRepository mockChatSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChatMockMvc;

    private Chat chat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chat createEntity(EntityManager em) {
        Chat chat = new Chat()
            .owner(DEFAULT_OWNER)
            .to(DEFAULT_TO)
            .subject(DEFAULT_SUBJECT)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastMessageDate(DEFAULT_LAST_MESSAGE_DATE)
            .type(DEFAULT_TYPE);
        return chat;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chat createUpdatedEntity(EntityManager em) {
        Chat chat = new Chat()
            .owner(UPDATED_OWNER)
            .to(UPDATED_TO)
            .subject(UPDATED_SUBJECT)
            .createdDate(UPDATED_CREATED_DATE)
            .lastMessageDate(UPDATED_LAST_MESSAGE_DATE)
            .type(UPDATED_TYPE);
        return chat;
    }

    @BeforeEach
    public void initTest() {
        chat = createEntity(em);
    }

    @Test
    @Transactional
    public void createChat() throws Exception {
        int databaseSizeBeforeCreate = chatRepository.findAll().size();
        // Create the Chat
        ChatDTO chatDTO = chatMapper.toDto(chat);
        restChatMockMvc.perform(post("/api/chats").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(chatDTO)))
            .andExpect(status().isCreated());

        // Validate the Chat in the database
        List<Chat> chatList = chatRepository.findAll();
        assertThat(chatList).hasSize(databaseSizeBeforeCreate + 1);
        Chat testChat = chatList.get(chatList.size() - 1);
        assertThat(testChat.getOwner()).isEqualTo(DEFAULT_OWNER);
        assertThat(testChat.getTo()).isEqualTo(DEFAULT_TO);
        assertThat(testChat.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testChat.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testChat.getLastMessageDate()).isEqualTo(DEFAULT_LAST_MESSAGE_DATE);
        assertThat(testChat.getType()).isEqualTo(DEFAULT_TYPE);

        // Validate the Chat in Elasticsearch
        verify(mockChatSearchRepository, times(1)).save(testChat);
    }

    @Test
    @Transactional
    public void createChatWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = chatRepository.findAll().size();

        // Create the Chat with an existing ID
        chat.setId(1L);
        ChatDTO chatDTO = chatMapper.toDto(chat);

        // An entity with an existing ID cannot be created, so this API call must fail
        restChatMockMvc.perform(post("/api/chats").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(chatDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Chat in the database
        List<Chat> chatList = chatRepository.findAll();
        assertThat(chatList).hasSize(databaseSizeBeforeCreate);

        // Validate the Chat in Elasticsearch
        verify(mockChatSearchRepository, times(0)).save(chat);
    }


    @Test
    @Transactional
    public void getAllChats() throws Exception {
        // Initialize the database
        chatRepository.saveAndFlush(chat);

        // Get all the chatList
        restChatMockMvc.perform(get("/api/chats?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chat.getId().intValue())))
            .andExpect(jsonPath("$.[*].owner").value(hasItem(DEFAULT_OWNER)))
            .andExpect(jsonPath("$.[*].to").value(hasItem(DEFAULT_TO)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastMessageDate").value(hasItem(DEFAULT_LAST_MESSAGE_DATE.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
    
    @Test
    @Transactional
    public void getChat() throws Exception {
        // Initialize the database
        chatRepository.saveAndFlush(chat);

        // Get the chat
        restChatMockMvc.perform(get("/api/chats/{id}", chat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(chat.getId().intValue()))
            .andExpect(jsonPath("$.owner").value(DEFAULT_OWNER))
            .andExpect(jsonPath("$.to").value(DEFAULT_TO))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastMessageDate").value(DEFAULT_LAST_MESSAGE_DATE.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingChat() throws Exception {
        // Get the chat
        restChatMockMvc.perform(get("/api/chats/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateChat() throws Exception {
        // Initialize the database
        chatRepository.saveAndFlush(chat);

        int databaseSizeBeforeUpdate = chatRepository.findAll().size();

        // Update the chat
        Chat updatedChat = chatRepository.findById(chat.getId()).get();
        // Disconnect from session so that the updates on updatedChat are not directly saved in db
        em.detach(updatedChat);
        updatedChat
            .owner(UPDATED_OWNER)
            .to(UPDATED_TO)
            .subject(UPDATED_SUBJECT)
            .createdDate(UPDATED_CREATED_DATE)
            .lastMessageDate(UPDATED_LAST_MESSAGE_DATE)
            .type(UPDATED_TYPE);
        ChatDTO chatDTO = chatMapper.toDto(updatedChat);

        restChatMockMvc.perform(put("/api/chats").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(chatDTO)))
            .andExpect(status().isOk());

        // Validate the Chat in the database
        List<Chat> chatList = chatRepository.findAll();
        assertThat(chatList).hasSize(databaseSizeBeforeUpdate);
        Chat testChat = chatList.get(chatList.size() - 1);
        assertThat(testChat.getOwner()).isEqualTo(UPDATED_OWNER);
        assertThat(testChat.getTo()).isEqualTo(UPDATED_TO);
        assertThat(testChat.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testChat.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testChat.getLastMessageDate()).isEqualTo(UPDATED_LAST_MESSAGE_DATE);
        assertThat(testChat.getType()).isEqualTo(UPDATED_TYPE);

        // Validate the Chat in Elasticsearch
        verify(mockChatSearchRepository, times(1)).save(testChat);
    }

    @Test
    @Transactional
    public void updateNonExistingChat() throws Exception {
        int databaseSizeBeforeUpdate = chatRepository.findAll().size();

        // Create the Chat
        ChatDTO chatDTO = chatMapper.toDto(chat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChatMockMvc.perform(put("/api/chats").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(chatDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Chat in the database
        List<Chat> chatList = chatRepository.findAll();
        assertThat(chatList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Chat in Elasticsearch
        verify(mockChatSearchRepository, times(0)).save(chat);
    }

    @Test
    @Transactional
    public void deleteChat() throws Exception {
        // Initialize the database
        chatRepository.saveAndFlush(chat);

        int databaseSizeBeforeDelete = chatRepository.findAll().size();

        // Delete the chat
        restChatMockMvc.perform(delete("/api/chats/{id}", chat.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Chat> chatList = chatRepository.findAll();
        assertThat(chatList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Chat in Elasticsearch
        verify(mockChatSearchRepository, times(1)).deleteById(chat.getId());
    }

    @Test
    @Transactional
    public void searchChat() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        chatRepository.saveAndFlush(chat);
        when(mockChatSearchRepository.search(queryStringQuery("id:" + chat.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(chat), PageRequest.of(0, 1), 1));

        // Search the chat
        restChatMockMvc.perform(get("/api/_search/chats?query=id:" + chat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chat.getId().intValue())))
            .andExpect(jsonPath("$.[*].owner").value(hasItem(DEFAULT_OWNER)))
            .andExpect(jsonPath("$.[*].to").value(hasItem(DEFAULT_TO)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastMessageDate").value(hasItem(DEFAULT_LAST_MESSAGE_DATE.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
}
