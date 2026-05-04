package drinkshop.service;

import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.service.validator.StocValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StocServiceIntegrationTest {

    private StocService stocService;
    
    // Validatorul este REAL acum (nu e Mock)
    private StocValidator validatorReal;

    @Mock
    private Repository<Integer, Stoc> stocRepoMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validatorReal = new StocValidator();
        // Construim Service-ul cu piesa reală și piesa falsă
        stocService = new StocService(stocRepoMock, validatorReal);
    }

    // --- TEST INTEGRARE 1: Validare Succes ---
    @Test
    void testAddStoc_ValidData_IntegrationSuccess() {
        Stoc stocValid = new Stoc(1, "Lapte", 20.0, 5.0);

        // Nu ar trebui să arunce nicio eroare
        assertDoesNotThrow(() -> stocService.add(stocValid));

        // Verificăm că a ajuns până la pasul de salvare în Repo
        verify(stocRepoMock, times(1)).save(stocValid);
    }

    // --- TEST INTEGRARE 2: Validare Eșuată (S + V real) ---
    @Test
    void testAddStoc_InvalidData_IntegrationFails() {
        // Creăm un stoc care încalcă regula: cantitate < stoc minim
        Stoc stocInvalid = new Stoc(1, "Cafea", 2.0, 10.0);

        // Verificăm dacă Service-ul aruncă eroarea venită de la Validatorul REAL
        ValidationException ex = assertThrows(ValidationException.class, () -> {
            stocService.add(stocInvalid);
        });

        assertTrue(ex.getMessage().contains("Cantitatea este sub stocul minim!"));

        // FOARTE IMPORTANT: Verificăm că NU s-a apelat salvarea (integrarea a oprit procesul corect)
        verify(stocRepoMock, never()).save(any());
    }

    // --- TEST INTEGRARE FINAL: S + V + R (Totul Real) ---
    @Test
    void testAddStoc_FullIntegration_RealFile() {
        // 1. Pregătim un repository real care scrie într-un fișier de test separat
        // *NOTĂ: Verifică dacă FileStocRepository primește numele fișierului în constructor
        drinkshop.repository.file.FileStocRepository repoReal = 
            new drinkshop.repository.file.FileStocRepository("data/stocuri_test.txt");
        
        // 2. Re-inițializăm service-ul cu TOATE piesele reale
        StocService serviceComplet = new StocService(repoReal, validatorReal);
        
        Stoc stocNou = new Stoc(99, "Miere", 50.0, 5.0);

        // ACT
        serviceComplet.add(stocNou);

        // ASSERT: Verificăm dacă a fost salvat cu succes în fișier (căutăm în listă)
        boolean gasit = serviceComplet.getAll().stream()
                .anyMatch(s -> s.getIngredient().equals("Miere"));
        
        assertTrue(gasit, "Stocul ar fi trebuit să fie salvat în fișierul real!");
    }
}