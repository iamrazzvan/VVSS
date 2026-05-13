package drinkshop.service;

import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.service.validator.StocValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StocServiceIntegrationTest {

    private StocService stocService; //[cite: 1]
    
    // Validatorul este REAL acum (nu e Mock)
    private StocValidator validatorReal; //[cite: 1]

    @Mock
    private Repository<Integer, Stoc> stocRepoMock; //[cite: 1]

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); //[cite: 1]
        validatorReal = new StocValidator(); //[cite: 1]
        stocService = new StocService(stocRepoMock, validatorReal); //[cite: 1]
    }

    // =========================================================
    // CATEGORIA 1: TESTE INTEGRARE - VALIDARE SUCCES
    // =========================================================

    @Test
    public void testAddStoc_ValidData_IntegrationSuccess() {
        Stoc stocValid = new Stoc(1, "Lapte", 20.0, 5.0); //[cite: 1]
        assertDoesNotThrow(() -> stocService.add(stocValid)); //[cite: 1]
        verify(stocRepoMock, times(1)).save(stocValid); //[cite: 1]
    }

    // Test NOU: Validare la limită
    @Test
    public void testAddStoc_ValidData_Boundary_IntegrationSuccess() {
        // Produs la limită: cantitatea este fix egală cu stocul minim (5.0)
        Stoc stocLaLimita = new Stoc(2, "Zahar", 5.0, 5.0);
        assertDoesNotThrow(() -> stocService.add(stocLaLimita));
        verify(stocRepoMock, times(1)).save(stocLaLimita);
    }


    // =========================================================
    // CATEGORIA 2: TESTE INTEGRARE - VALIDARE EȘUATĂ (MOCK REPO)
    // =========================================================

    @Test
    public void testAddStoc_InvalidData_IntegrationFails() {
        Stoc stocInvalid = new Stoc(1, "Cafea", 2.0, 10.0); //[cite: 1]
        ValidationException ex = assertThrows(ValidationException.class, () -> { //[cite: 1]
            stocService.add(stocInvalid); //[cite: 1]
        });
        assertTrue(ex.getMessage().contains("Cantitatea este sub stocul minim")); // Adaptat pentru siguranță[cite: 1]
        verify(stocRepoMock, never()).save(any()); //[cite: 1]
    }

    // Test NOU: Validare pe un alt parametru (Nume gol)
    @Test
    public void testAddStoc_InvalidName_IntegrationFails() {
        // Trimitem un produs cu nume gol ("")
        Stoc stocFaraNume = new Stoc(3, "", 20.0, 5.0);
        assertThrows(ValidationException.class, () -> {
            stocService.add(stocFaraNume);
        });
        verify(stocRepoMock, never()).save(any());
    }


    // =========================================================
    // CATEGORIA 3: TESTE INTEGRARE FINALĂ (TOTUL REAL)
    // =========================================================

    @Test
    public void testAddStoc_FullIntegration_RealFile() {
        drinkshop.repository.file.FileStocRepository repoReal = 
            new drinkshop.repository.file.FileStocRepository("data/stocuri_test.txt"); //[cite: 1]
        
        StocService serviceComplet = new StocService(repoReal, validatorReal); //[cite: 1]
        Stoc stocNou = new Stoc(99, "Miere", 50.0, 5.0); //[cite: 1]

        serviceComplet.add(stocNou); //[cite: 1]

        boolean gasit = serviceComplet.getAll().stream() //[cite: 1]
                .anyMatch(s -> s.getIngredient().equals("Miere")); //[cite: 1]
        
        assertTrue(gasit, "Stocul ar fi trebuit să fie salvat în fișierul real!"); //[cite: 1]
    }

    // Test NOU: Integrare finală cu date invalide
    @Test
    public void testAddStoc_InvalidData_FullIntegration() {
        // Testăm că sistemul complet conectat blochează datele proaste
        drinkshop.repository.file.FileStocRepository repoReal = 
            new drinkshop.repository.file.FileStocRepository("data/stocuri_test.txt");
        
        StocService serviceComplet = new StocService(repoReal, validatorReal);
        int dimensiuneInitiala = serviceComplet.getAll().size();
        
        // Date eronate: cantitate negativă
        Stoc stocGunoi = new Stoc(100, "Otrava", -10.0, 5.0);

        assertThrows(ValidationException.class, () -> {
            serviceComplet.add(stocGunoi);
        });

        // Verificăm că fișierul real nu a fost extins cu înregistrări greșite
        assertEquals(dimensiuneInitiala, serviceComplet.getAll().size(), "Fișierul real nu trebuia să fie modificat!");
    }
}