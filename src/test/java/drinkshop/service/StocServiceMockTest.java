package drinkshop.service;

import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.service.validator.StocValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//(1) V <--- S ---> R și ulterior integrare top-down, breadth first
@ExtendWith(MockitoExtension.class)
public class StocServiceMockTest {

    // 1. Creăm "angajații falși" (Mock-uri)
    @Mock
    private Repository<Integer, Stoc> stocRepoMock;

    @Mock
    private StocValidator validatorMock;

    // 2. Injectăm clonele în Managerul nostru real pe care vrem să-l testăm
    @InjectMocks
    private StocService stocService;

    private Stoc stocDeTest;

    @BeforeEach
    void setUp() {
        // Aici pregătim o entitate simplă înainte de fiecare test
        // *NOTĂ: Dacă constructorul tău din clasa Stoc e diferit, modifică parametrii de aici!
        stocDeTest = new Stoc(1, "Zahar", 100.0, 10.0);
    }

    // --- TESTUL 1: Verificăm că metoda add() apelează corect validarea și salvarea ---
    @Test
    void testAddStoc_ApeleazaDependenciesCorect() {
        // ACT (Acțiunea): Aici doar apelăm metoda
        stocService.add(stocDeTest);

        // VERIFY (Verificarea): Îi întrebăm pe "angajații falși" dacă au primit ordinul corect
        // Verificăm dacă validatorMock a fost apelat fix o dată cu stocDeTest
        verify(validatorMock, times(1)).validate(stocDeTest);
        
        // Verificăm dacă repo-ul a fost apelat fix o dată pentru salvare
        verify(stocRepoMock, times(1)).save(stocDeTest);
    }

    // --- TESTUL 2: Verificăm metoda getAll() ---
    @Test
    void testGetAll_ReturneazaListaCorecta() {
        // ARRANGE (Pregătirea): Învățăm clona ce trebuie să răspundă când o întreabă Service-ul
        List<Stoc> listaFalsa = Arrays.asList(stocDeTest, new Stoc(2, "Faina", 50.0, 5.0));
        when(stocRepoMock.findAll()).thenReturn(listaFalsa);

        // ACT (Acțiunea)
        List<Stoc> rezultat = stocService.getAll();

        // ASSERT (Confirmarea): Verificăm dacă Service-ul ne dă înapoi ce trebuie
        assertEquals(2, rezultat.size(), "Lista ar trebui să aibă 2 elemente");
        
        // VERIFY: Verificăm că Service-ul chiar a cerut lista de la Repo
        verify(stocRepoMock, times(1)).findAll();
    }
}