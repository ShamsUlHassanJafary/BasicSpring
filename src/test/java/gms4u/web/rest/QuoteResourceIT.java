package gms4u.web.rest;

import gms4u.Gms4UApp;
import gms4u.domain.Booking;
import gms4u.domain.Quote;
import gms4u.repository.QuoteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link QuoteResource} REST controller.
 */
@SpringBootTest(classes = Gms4UApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class QuoteResourceIT {

    private static final LocalDate DEFAULT_QUOTE_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate UPDATED_QUOTE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final BigDecimal DEFAULT_QUOTE_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_QUOTE_TOTAL = new BigDecimal(2);

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuoteMockMvc;

    private Quote quote;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Quote createEntity(EntityManager em) {
        Quote quote = new Quote()
            .quoteDate(DEFAULT_QUOTE_DATE)
            .quoteTotal(DEFAULT_QUOTE_TOTAL);
        return quote;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Quote createUpdatedEntity(EntityManager em) {
        Quote quote = new Quote()
            .quoteDate(UPDATED_QUOTE_DATE)
            .quoteTotal(UPDATED_QUOTE_TOTAL);
        return quote;
    }

    @BeforeEach
    public void initTest() {
        quote = createEntity(em);
    }

    @Test
    @Transactional
    public void createQuote() throws Exception {
        int databaseSizeBeforeCreate = quoteRepository.findAll().size();
        // Create the Quote
        restQuoteMockMvc.perform(post("/api/quotes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(quote)))
            .andExpect(status().isCreated());

        // Validate the Quote in the database
        List<Quote> quoteList = quoteRepository.findAll();
        assertThat(quoteList).hasSize(databaseSizeBeforeCreate + 1);
        Quote testQuote = quoteList.get(quoteList.size() - 1);
        assertThat(testQuote.getQuoteDate()).isEqualTo(DEFAULT_QUOTE_DATE);
        assertThat(testQuote.getQuoteTotal()).isEqualTo(DEFAULT_QUOTE_TOTAL);
    }

    @Test
    @Transactional
    public void createQuoteWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = quoteRepository.findAll().size();

        // Create the Quote with an existing ID
        quote.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuoteMockMvc.perform(post("/api/quotes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(quote)))
            .andExpect(status().isBadRequest());

        // Validate the Quote in the database
        List<Quote> quoteList = quoteRepository.findAll();
        assertThat(quoteList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllQuotes() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);

        // Get all the quoteList
        restQuoteMockMvc.perform(get("/api/quotes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quote.getId().intValue())))
            .andExpect(jsonPath("$.[*].quoteDate").value(hasItem(DEFAULT_QUOTE_DATE.toString())))
            .andExpect(jsonPath("$.[*].quoteTotal").value(hasItem(DEFAULT_QUOTE_TOTAL.intValue())));
    }

    @Test
    @Transactional
    public void getQuote() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);

        // Get the quote
        restQuoteMockMvc.perform(get("/api/quotes/{id}", quote.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(quote.getId().intValue()))
            .andExpect(jsonPath("$.quoteDate").value(DEFAULT_QUOTE_DATE.toString()))
            .andExpect(jsonPath("$.quoteTotal").value(DEFAULT_QUOTE_TOTAL.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingQuote() throws Exception {
        // Get the quote
        restQuoteMockMvc.perform(get("/api/quotes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateQuote() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);

        int databaseSizeBeforeUpdate = quoteRepository.findAll().size();

        // Update the quote
        Quote updatedQuote = quoteRepository.findById(quote.getId()).get();
        // Disconnect from session so that the updates on updatedQuote are not directly saved in db
        em.detach(updatedQuote);
        updatedQuote
            .quoteDate(UPDATED_QUOTE_DATE)
            .quoteTotal(UPDATED_QUOTE_TOTAL);

        restQuoteMockMvc.perform(put("/api/quotes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedQuote)))
            .andExpect(status().isOk());

        // Validate the Quote in the database
        List<Quote> quoteList = quoteRepository.findAll();
        assertThat(quoteList).hasSize(databaseSizeBeforeUpdate);
        Quote testQuote = quoteList.get(quoteList.size() - 1);
        assertThat(testQuote.getQuoteDate()).isEqualTo(UPDATED_QUOTE_DATE);
        assertThat(testQuote.getQuoteTotal()).isEqualTo(UPDATED_QUOTE_TOTAL);
    }

    @Test
    @Transactional
    public void updateNonExistingQuote() throws Exception {
        int databaseSizeBeforeUpdate = quoteRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuoteMockMvc.perform(put("/api/quotes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(quote)))
            .andExpect(status().isBadRequest());

        // Validate the Quote in the database
        List<Quote> quoteList = quoteRepository.findAll();
        assertThat(quoteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteQuote() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);

        int databaseSizeBeforeDelete = quoteRepository.findAll().size();

        // Delete the quote
        restQuoteMockMvc.perform(delete("/api/quotes/{id}", quote.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Quote> quoteList = quoteRepository.findAll();
        assertThat(quoteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
