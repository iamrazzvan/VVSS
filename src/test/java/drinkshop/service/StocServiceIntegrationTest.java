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

    @Test
public void testAddStoc_ValidData_IntegrationSuccess() {
    // Îți creezi local componentele ca să fii sigur că nu sunt null
    Repository<Integer, Stoc> stocRepoMock = mock(Repository.class);
    StocValidator validatorReal = new StocValidator();
    StocService stocService = new StocService(stocRepoMock, validatorReal);

    Stoc stocValid = new Stoc(1, "Lapte", 20.0, 5.0);
    assertDoesNotThrow(() -> stocService.add(stocValid));
    verify(stocRepoMock, times(1)).save(stocValid);
}

@Test
public void testAddStoc_ValidData_Boundary_IntegrationSuccess() {
    Repository<Integer, Stoc> stocRepoMock = mock(Repository.class);
    StocValidator validatorReal = new StocValidator();
    StocService stocService = new StocService(stocRepoMock, validatorReal);

    Stoc stocLaLimita = new Stoc(2, "Zahar", 5.0, 5.0);
    assertDoesNotThrow(() -> stocService.add(stocLaLimita));
    verify(stocRepoMock, times(1)).save(stocLaLimita);
}

@Test
public void testAddStoc_InvalidData_IntegrationFails() {
    Repository<Integer, Stoc> stocRepoMock = mock(Repository.class);
    StocValidator validatorReal = new StocValidator();
    StocService stocService = new StocService(stocRepoMock, validatorReal);

    Stoc stocInvalid = new Stoc(1, "Cafea", 2.0, 10.0);
    ValidationException ex = assertThrows(ValidationException.class, () -> {
        stocService.add(stocInvalid);
    });
    assertTrue(ex.getMessage().contains("Cantitatea este sub stocul minim"));
    verify(stocRepoMock, never()).save(any());
}

@Test
public void testAddStoc_InvalidName_IntegrationFails() {
    Repository<Integer, Stoc> stocRepoMock = mock(Repository.class);
    StocValidator validatorReal = new StocValidator();
    StocService stocService = new StocService(stocRepoMock, validatorReal);

    Stoc stocFaraNume = new Stoc(3, "", 20.0, 5.0);
    assertThrows(ValidationException.class, () -> {
        stocService.add(stocFaraNume);
    });
    verify(stocRepoMock, never()).save(any());
}


    // =========================================================
    // CATEGORIA 3: TESTE INTEGRARE FINALĂ (TOTUL REAL)
    // =========================================================

    // =========================================================
    // CATEGORIA 3: TESTE INTEGRARE FINALĂ (TOTUL REAL)
    // =========================================================

    @Test
    public void testAddStoc_FullIntegration_RealFile() {
        drinkshop.repository.file.FileStocRepository repoReal = 
            new drinkshop.repository.file.FileStocRepository("data/stocuri_test.txt");
        
        // DECLARĂ VALIDATORUL LOCAL:
        StocValidator validatorReal = new StocValidator();
        
        StocService serviceComplet = new StocService(repoReal, validatorReal);
        Stoc stocNou = new Stoc(99, "Miere", 50.0, 5.0);

        serviceComplet.add(stocNou);

        boolean gasit = serviceComplet.getAll().stream()
                .anyMatch(s -> s.getIngredient().equals("Miere"));
        
        assertTrue(gasit, "Stocul ar fi trebuit să fie salvat în fișierul real!");
    }

    // Test NOU: Integrare finală cu date invalide
    @Test
    public void testAddStoc_InvalidData_FullIntegration() {
        drinkshop.repository.file.FileStocRepository repoReal = 
            new drinkshop.repository.file.FileStocRepository("data/stocuri_test.txt");
        
        // DECLARĂ VALIDATORUL LOCAL ȘI AICI:
        StocValidator validatorReal = new StocValidator();
        
        StocService serviceComplet = new StocService(repoReal, validatorReal);
        int dimensiuneInitiala = serviceComplet.getAll().size();
        
        Stoc stocGunoi = new Stoc(100, "Otrava", -10.0, 5.0);

        assertThrows(ValidationException.class, () -> {
            serviceComplet.add(stocGunoi);
        });

        assertEquals(dimensiuneInitiala, serviceComplet.getAll().size(), "Fișierul real nu trebuia să fie modificat!");
    }
}