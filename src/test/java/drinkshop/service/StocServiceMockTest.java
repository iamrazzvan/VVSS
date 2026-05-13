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
//import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StocServiceMockTest {

    @Mock
    private Repository<Integer, Stoc> stocRepoMock;

    @Mock
    private StocValidator validatorMock;

    @InjectMocks
    private StocService stocService;

    private Stoc stocDeTest;

    @BeforeEach
    void setUp() {
        stocDeTest = new Stoc(1, "Zahar", 100.0, 10.0);
    }

    @Test
    public void testAddStoc_ApeleazaDependenciesCorect() {
        stocService.add(stocDeTest);
        verify(validatorMock, times(1)).validate(stocDeTest);
        verify(stocRepoMock, times(1)).save(stocDeTest);
    }

    @Test
    public void testGetAll_ReturneazaListaCorecta() {
        List<Stoc> listaFalsa = Arrays.asList(stocDeTest, new Stoc(2, "Faina", 50.0, 5.0));
        when(stocRepoMock.findAll()).thenReturn(listaFalsa);

        List<Stoc> rezultat = stocService.getAll();

        assertEquals(2, rezultat.size());
        verify(stocRepoMock, times(1)).findAll();
    }

    @Test
    public void testGetAll_CandNuExistaDate_ReturneazaListaGoala() {
        when(stocRepoMock.findAll()).thenReturn(new ArrayList<>());
        List<Stoc> rezultat = stocService.getAll();
        assertTrue(rezultat.isEmpty());
        verify(stocRepoMock, times(1)).findAll();
    }

    @Test
    public void testDeleteStoc_ApeleazaRepoCorect() {
        Integer idDeSters = 1;
        stocService.delete(idDeSters);
        verify(stocRepoMock, times(1)).delete(idDeSters);
    }

    @Test
    public void testAddStoc_CandValidareaEsueaza_NuSeSalveaza() {
        doThrow(new RuntimeException("Validare esuata")).when(validatorMock).validate(any());
        assertThrows(RuntimeException.class, () -> stocService.add(stocDeTest));
        verify(stocRepoMock, never()).save(any());
    }
}