package drinkshop.service;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.file.FileStocRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Teste WBT (White Box Testing) pentru metoda consuma() din StocService.
 *
 * Elemente structurale investigate:
 *   1. D1: if (reteta == null || reteta.getIngrediente() == null || reteta.getIngrediente().isEmpty())
 *   2. D2: if (!areSuficient(reteta))
 *   3. L1: for (IngredientReteta e : reteta.getIngrediente())
 *   4. D3: if (ingredient == null || ingredient.isBlank() || necesar <= 0)
 *   5. L2: for (Stoc s : ingredienteStoc)
 *   6. D4: if (ramas <= 0)
 *
 * Criterii de acoperire urmarite:
 *   1. SC  - Statement Coverage
 *   2. DC  - Decision Coverage
 *   3. CC  - Condition Coverage
 *   4. DCC - Decision/Condition Coverage
 *   5. MCC - Multiple Condition Coverage
 *   6. APC - All Path Coverage
 *   7. LC  - Simple Loop Coverage
 *
 * Adnotari utilizate:
 *   1. @DisplayName - descriere lizibila a clasei si a testelor
 *   2. @Tag         - categorizare teste (SC / DC / CC / DCC / MCC / APC / LC)
 *   3. @TestMethodOrder + @Order - ordinea de executie a testelor
 */
@DisplayName("StocService - consuma() - WBT")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StocServiceTest {

    private FileStocRepository stocRepo;
    private StocService service;

    @BeforeEach
    void setUp() {
        stocRepo = mock(FileStocRepository.class);
        service = Mockito.spy(new StocService(stocRepo));
    }

    // =========================================================================
    // WBT - CFG / Coverage
    //
    // D1: reteta == null || reteta.getIngrediente() == null || reteta.getIngrediente().isEmpty()
    // D2: !areSuficient(reteta)
    // D3: ingredient == null || ingredient.isBlank() || necesar <= 0
    // D4: ramas <= 0
    //
    // L1: bucla peste ingredientele retetei
    // L2: bucla peste loturile de stoc
    // =========================================================================

    @Test
    @Order(1)
    @Tag("DC")
    @Tag("CC")
    @Tag("DCC")
    @Tag("MCC")
    @DisplayName("TC1_WBT Invalid - D1: reteta=null => IllegalArgumentException")
    void testConsuma_Invalid_RetetaNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.consuma(null),
                "Ar trebui sa arunce IllegalArgumentException pentru reteta null");

        assertTrue(ex.getMessage().contains("Reteta invalida"),
                "Mesajul exceptiei trebuie sa contina 'Reteta invalida'");

        verify(stocRepo, never()).findAll();
        verify(stocRepo, never()).update(any());
    }

    @Test
    @Order(2)
    @Tag("CC")
    @Tag("DCC")
    @Tag("MCC")
    @DisplayName("TC2_WBT Invalid - D1: reteta.getIngrediente()=null => IllegalArgumentException")
    void testConsuma_Invalid_ListaIngredienteNull() {
        Reteta reteta = mock(Reteta.class);
        when(reteta.getIngrediente()).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.consuma(reteta),
                "Ar trebui sa arunce IllegalArgumentException pentru lista de ingrediente null");

        assertTrue(ex.getMessage().contains("Reteta invalida"),
                "Mesajul exceptiei trebuie sa contina 'Reteta invalida'");

        verify(stocRepo, never()).findAll();
        verify(stocRepo, never()).update(any());
    }

    @Test
    @Order(3)
    @Tag("LC")
    @Tag("CC")
    @Tag("DCC")
    @Tag("MCC")
    @DisplayName("TC3_WBT Invalid - D1: reteta.getIngrediente().isEmpty() => IllegalArgumentException")
    void testConsuma_Invalid_ListaIngredienteVida() {
        Reteta reteta = mock(Reteta.class);
        when(reteta.getIngrediente()).thenReturn(List.of());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.consuma(reteta),
                "Ar trebui sa arunce IllegalArgumentException pentru lista de ingrediente vida");

        assertTrue(ex.getMessage().contains("Reteta invalida"),
                "Mesajul exceptiei trebuie sa contina 'Reteta invalida'");

        verify(stocRepo, never()).findAll();
        verify(stocRepo, never()).update(any());
    }

    @Test
    @Order(4)
    @Tag("DC")
    @Tag("APC")
    @DisplayName("TC4_WBT Invalid - D2: stoc insuficient => IllegalStateException")
    void testConsuma_Invalid_StocInsuficient() {
        IngredientReteta ir = mock(IngredientReteta.class);
        when(ir.getDenumire()).thenReturn("Cafea");
        when(ir.getCantitate()).thenReturn(Double.valueOf(10.0));


        Reteta reteta = mock(Reteta.class);
        when(reteta.getIngrediente()).thenReturn(List.of(ir));

        doReturn(false).when(service).areSuficient(reteta);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.consuma(reteta),
                "Ar trebui sa arunce IllegalStateException daca stocul este insuficient");

        assertTrue(ex.getMessage().contains("Stoc insuficient"),
                "Mesajul exceptiei trebuie sa contina 'Stoc insuficient'");

        verify(stocRepo, never()).findAll();
        verify(stocRepo, never()).update(any());
    }

    @Test
    @Order(5)
    @Tag("CC")
    @Tag("DCC")
    @Tag("MCC")
    @Tag("APC")
    @DisplayName("TC5_WBT Invalid - D3: ingredient=null => IllegalArgumentException")
    void testConsuma_Invalid_IngredientNull() {
        IngredientReteta ir = mock(IngredientReteta.class);
        when(ir.getDenumire()).thenReturn(null);
        when(ir.getCantitate()).thenReturn(Double.valueOf(10.0));


        Reteta reteta = mock(Reteta.class);
        when(reteta.getIngrediente()).thenReturn(List.of(ir));

        doReturn(true).when(service).areSuficient(reteta);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.consuma(reteta),
                "Ar trebui sa arunce IllegalArgumentException pentru ingredient null");

        assertTrue(ex.getMessage().contains("Ingredient invalid"),
                "Mesajul exceptiei trebuie sa contina 'Ingredient invalid in reteta'");

        verify(stocRepo, never()).findAll();
        verify(stocRepo, never()).update(any());
    }

    @Test
    @Order(6)
    @Tag("CC")
    @Tag("DCC")
    @Tag("MCC")
    @Tag("APC")
    @DisplayName("TC6_WBT Invalid - D3: ingredient blank => IllegalArgumentException")
    void testConsuma_Invalid_IngredientBlank() {
        IngredientReteta ir = mock(IngredientReteta.class);
        when(ir.getDenumire()).thenReturn("   ");
        when(ir.getCantitate()).thenReturn(Double.valueOf(10.0));


        Reteta reteta = mock(Reteta.class);
        when(reteta.getIngrediente()).thenReturn(List.of(ir));

        doReturn(true).when(service).areSuficient(reteta);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.consuma(reteta),
                "Ar trebui sa arunce IllegalArgumentException pentru ingredient blank");

        assertTrue(ex.getMessage().contains("Ingredient invalid"),
                "Mesajul exceptiei trebuie sa contina 'Ingredient invalid in reteta'");

        verify(stocRepo, never()).findAll();
        verify(stocRepo, never()).update(any());
    }

    @Test
    @Order(7)
    @Tag("CC")
    @Tag("DCC")
    @Tag("MCC")
    @Tag("APC")
    @DisplayName("TC7_WBT Invalid - D3: necesar<=0 => IllegalArgumentException")
    void testConsuma_Invalid_CantitateNecesarZero() {
        IngredientReteta ir = mock(IngredientReteta.class);
        when(ir.getDenumire()).thenReturn("Cafea");
        when(ir.getCantitate()).thenReturn(Double.valueOf(0.0));


        Reteta reteta = mock(Reteta.class);
        when(reteta.getIngrediente()).thenReturn(List.of(ir));

        doReturn(true).when(service).areSuficient(reteta);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.consuma(reteta),
                "Ar trebui sa arunce IllegalArgumentException pentru cantitate necesara <= 0");

        assertTrue(ex.getMessage().contains("Ingredient invalid"),
                "Mesajul exceptiei trebuie sa contina 'Ingredient invalid in reteta'");

        verify(stocRepo, never()).findAll();
        verify(stocRepo, never()).update(any());
    }

    @Test
    @Order(8)
    @Tag("SC")
    @Tag("DC")
    @Tag("APC")
    @Tag("LC")
    @DisplayName("TC8_WBT Valid - un ingredient, un singur lot => consum complet dintr-un singur stoc")
    void testConsuma_Valid_UnIngredient_UnLot() {
        IngredientReteta ir = mock(IngredientReteta.class);
        when(ir.getDenumire()).thenReturn("Cafea");
        when(ir.getCantitate()).thenReturn(Double.valueOf(30.0));

        Reteta reteta = mock(Reteta.class);
        when(reteta.getIngrediente()).thenReturn(List.of(ir));

        Stoc s1 = mock(Stoc.class);
        when(s1.getIngredient()).thenReturn("Cafea");
        when(s1.getCantitate()).thenReturn(Double.valueOf(50.0));


        when(stocRepo.findAll()).thenReturn(List.of(s1));
        doReturn(true).when(service).areSuficient(reteta);

        service.consuma(reteta);

        verify(s1).setCantitate(20);
        verify(stocRepo, times(1)).update(s1);
    }

    @Test
    @Order(9)
    @Tag("LC")
    @Tag("SC")
    @Tag("APC")
    @DisplayName("TC9_WBT Valid - un ingredient, mai multe loturi => consum distribuit pe doua stocuri")
    void testConsuma_Valid_UnIngredient_MaiMulteLoturi() {
        IngredientReteta ir = mock(IngredientReteta.class);
        when(ir.getDenumire()).thenReturn("Lapte");
        when(ir.getCantitate()).thenReturn(Double.valueOf(70.0));


        Reteta reteta = mock(Reteta.class);
        when(reteta.getIngrediente()).thenReturn(List.of(ir));

        Stoc s1 = mock(Stoc.class);
        when(s1.getIngredient()).thenReturn("Lapte");
        when(s1.getCantitate()).thenReturn(Double.valueOf(30.0));


        Stoc s2 = mock(Stoc.class);
        when(s2.getIngredient()).thenReturn("Lapte");
        when(s2.getCantitate()).thenReturn(Double.valueOf(50.0));


        when(stocRepo.findAll()).thenReturn(List.of(s1, s2));
        doReturn(true).when(service).areSuficient(reteta);

        service.consuma(reteta);

        verify(s1).setCantitate(0);
        verify(s2).setCantitate(10);
        verify(stocRepo).update(s1);
        verify(stocRepo).update(s2);
    }

    @Test
    @Order(10)
    @Tag("DC")
    @Tag("LC")
    @Tag("APC")
    @DisplayName("TC10_WBT Valid - D4=true: ramas devine 0 si se executa break")
    void testConsuma_Valid_ExecutaBreakCandRamasAjungeLaZero() {
        IngredientReteta ir = mock(IngredientReteta.class);
        when(ir.getDenumire()).thenReturn("Cacao");
        when(ir.getCantitate()).thenReturn(Double.valueOf(20.0));

        Reteta reteta = mock(Reteta.class);
        when(reteta.getIngrediente()).thenReturn(List.of(ir));

        Stoc s1 = mock(Stoc.class);
        when(s1.getIngredient()).thenReturn("Cacao");
        when(s1.getCantitate()).thenReturn(Double.valueOf(50.0));

        Stoc s2 = mock(Stoc.class);
        when(s2.getIngredient()).thenReturn("Cacao");
        when(s2.getCantitate()).thenReturn(Double.valueOf(40.0));

        when(stocRepo.findAll()).thenReturn(List.of(s1, s2));
        doReturn(true).when(service).areSuficient(reteta);

        service.consuma(reteta);

        verify(s1).setCantitate(30);
        verify(stocRepo, times(1)).update(s1);
        verify(stocRepo, never()).update(s2);
        verify(s2, never()).setCantitate(anyInt());
    }

    @Test
    @Order(11)
    @Tag("LC")
    @Tag("SC")
    @Tag("APC")
    @DisplayName("TC11_WBT Valid - mai multe ingrediente => bucla exterioara ruleaza de mai multe ori")
    void testConsuma_Valid_MaiMulteIngrediente() {
        IngredientReteta ir1 = mock(IngredientReteta.class);
        when(ir1.getDenumire()).thenReturn("Cafea");
        when(ir1.getCantitate()).thenReturn(Double.valueOf(20.0));

        IngredientReteta ir2 = mock(IngredientReteta.class);
        when(ir2.getDenumire()).thenReturn("Lapte");
        when(ir2.getCantitate()).thenReturn(Double.valueOf(10.0));

        Reteta reteta = mock(Reteta.class);
        when(reteta.getIngrediente()).thenReturn(List.of(ir1, ir2));

        Stoc s1 = mock(Stoc.class);
        when(s1.getIngredient()).thenReturn("Cafea");
        when(s1.getCantitate()).thenReturn(Double.valueOf(50.0));

        Stoc s2 = mock(Stoc.class);
        when(s2.getIngredient()).thenReturn("Lapte");
        when(s2.getCantitate()).thenReturn(Double.valueOf(30.0));

        when(stocRepo.findAll()).thenReturn(List.of(s1, s2));
        doReturn(true).when(service).areSuficient(reteta);

        service.consuma(reteta);

        verify(s1).setCantitate(30);
        verify(s2).setCantitate(20);
        verify(stocRepo).update(s1);
        verify(stocRepo).update(s2);
    }

    @Test
    @Order(12)
    @Tag("LC")
    @Tag("APC")
    @DisplayName("TC12_WBT Valid - ingredient valid dar fara loturi compatibile in stoc => bucla interioara 0 iteratii")
    void testConsuma_Valid_FaraLoturiPentruIngredient() {
        IngredientReteta ir = mock(IngredientReteta.class);
        when(ir.getDenumire()).thenReturn("Matcha");
        when(ir.getCantitate()).thenReturn(Double.valueOf(10.0));

        Reteta reteta = mock(Reteta.class);
        when(reteta.getIngrediente()).thenReturn(List.of(ir));

        Stoc s1 = mock(Stoc.class);
        when(s1.getIngredient()).thenReturn("Cafea");

        when(stocRepo.findAll()).thenReturn(List.of(s1));
        doReturn(true).when(service).areSuficient(reteta);

        assertDoesNotThrow(() -> service.consuma(reteta),
                "Nu ar trebui sa arunce exceptie daca ingredientul e valid, dar nu exista loturi filtrate");

        verify(stocRepo, never()).update(any());
    }
}