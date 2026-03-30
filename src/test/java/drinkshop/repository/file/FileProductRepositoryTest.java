package drinkshop.repository.file;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste BBT (Black Box Testing) pentru metoda save() din FileProductRepository.
 *
 * Parametri investigati:
 *   1. pret (double) - constrangere: pret > 0
 *   2. nume (String) - constrangere: nume != null && !nume.isBlank()
 *
 * Adnotari utilizate (diferite de @Before/AfterAll, @Before/AfterEach, @Test):
 *   1. @DisplayName   - descriere lizibila a testelor
 *   2. @TempDir        - director temporar pentru fisierul repository-ului
 *   3. @Tag            - categorizare teste (ECP / BVA)
 *   4. @TestMethodOrder + @Order - ordinea de executie a testelor
 */
@DisplayName("FileProductRepository - save() - BBT")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileProductRepositoryTest {

    @TempDir
    Path tempDir;

    private FileProductRepository repository;
    private ProductValidator validator;

    @BeforeEach
    void setUp() throws IOException {
        File tempFile = tempDir.resolve("test_products.txt").toFile();
        tempFile.createNewFile();
        repository = new FileProductRepository(tempFile.getAbsolutePath());
        validator = new ProductValidator();
    }

    // =========================================================================
    // ECP (Equivalence Class Partitioning)
    //
    // EC1: id > 0          (valid)    |  EC2: id <= 0         (invalid)
    // EC3: nume non-blank  (valid)    |  EC4: nume empty/null (invalid)
    // EC5: pret > 0        (valid)    |  EC6: pret <= 0       (invalid)
    // =========================================================================

    @Test
    @Order(1)
    @Tag("ECP")
    @DisplayName("TC1_ECP Valid - EC{1,3,5}: id=1, nume='Espresso', pret=25 => produs adaugat")
    void testSave_ECP_Valid() {
        Product product = new Product(1, "Espresso", 25,
                CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        validator.validate(product);
        Product saved = repository.save(product);

        assertNotNull(saved, "Produsul salvat nu trebuie sa fie null");
        assertEquals("Espresso", saved.getNume());
        assertEquals(25, saved.getPret(), 0.001);
        assertEquals(product, repository.findOne(1),
                "Produsul trebuie sa fie gasit in repository dupa save");
        assertEquals(1, repository.findAll().size(),
                "Repository-ul trebuie sa contina exact un produs");
    }

    @Test
    @Order(2)
    @Tag("ECP")
    @DisplayName("TC2_ECP Invalid - EC{1,3,6}: id=2, nume='Pumpkin-spiced latte', pret=-25 => Pret invalid!")
    void testSave_ECP_Invalid_PretNegativ() {
        Product product = new Product(2, "Pumpkin-spiced latte", -25,
                CategorieBautura.ICED_COFFEE, TipBautura.BASIC);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validate(product),
                "Ar trebui sa arunce ValidationException pentru pret negativ");
        assertTrue(ex.getMessage().contains("Pret invalid"),
                "Mesajul exceptiei trebuie sa contina 'Pret invalid'");
    }

    @Test
    @Order(3)
    @Tag("ECP")
    @DisplayName("TC3_ECP Invalid - EC{1,4,5}: id=3, nume='', pret=25 => Numele nu poate fi gol!")
    void testSave_ECP_Invalid_NumeGol() {
        Product product = new Product(3, "", 25,
                CategorieBautura.ICED_COFFEE, TipBautura.BASIC);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validate(product),
                "Ar trebui sa arunce ValidationException pentru nume gol");
        assertTrue(ex.getMessage().contains("Numele nu poate fi gol"),
                "Mesajul exceptiei trebuie sa contina 'Numele nu poate fi gol'");
    }

    @Test
    @Order(4)
    @Tag("ECP")
    @DisplayName("TC4_ECP Invalid - EC{2,3,6}: id=-1, nume='Pumpkin-spiced latte', pret=-25 => ID invalid + Pret invalid")
    void testSave_ECP_Invalid_IdSiPret() {
        Product product = new Product(-1, "Pumpkin-spiced latte", -25,
                CategorieBautura.ICED_COFFEE, TipBautura.BASIC);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validate(product),
                "Ar trebui sa arunce ValidationException pentru id si pret invalide");
        assertTrue(ex.getMessage().contains("ID invalid"),
                "Mesajul exceptiei trebuie sa contina 'ID invalid'");
        assertTrue(ex.getMessage().contains("Pret invalid"),
                "Mesajul exceptiei trebuie sa contina 'Pret invalid'");
    }

    @Test
    @Order(5)
    @Tag("ECP")
    @DisplayName("TC5_ECP Valid - EC{1,3,5}: id=7, nume='Matcha', pret=30 => produs adaugat")
    void testSave_ECP_Valid_Matcha() {
        Product product = new Product(7, "Matcha", 30,
                CategorieBautura.SPECIAL_COFFEE, TipBautura.PLANT_BASED);

        validator.validate(product);
        Product saved = repository.save(product);

        assertNotNull(saved, "Produsul salvat nu trebuie sa fie null");
        assertEquals("Matcha", saved.getNume());
        assertEquals(30, saved.getPret(), 0.001);
        assertEquals(product, repository.findOne(7),
                "Produsul trebuie sa fie gasit in repository dupa save");
        assertEquals(1, repository.findAll().size(),
                "Repository-ul trebuie sa contina exact un produs");
    }

    @Test
    @Order(6)
    @Tag("ECP")
    @DisplayName("TC6_ECP Invalid - EC{1,3,6}: id=7, nume='Matcha', pret=-67 => Pret invalid!")
    void testSave_ECP_Invalid_Matcha_PretNegativ() {
        Product product = new Product(7, "Matcha", -67,
                CategorieBautura.SPECIAL_COFFEE, TipBautura.PLANT_BASED);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validate(product),
                "Ar trebui sa arunce ValidationException pentru pret negativ");
        assertTrue(ex.getMessage().contains("Pret invalid"),
                "Mesajul exceptiei trebuie sa contina 'Pret invalid'");
    }

    // =========================================================================
    // BVA (Boundary Value Analysis)
    //
    // Crt.1 - id > 0:       BVA cond 01: id=1, 02: id=0, 03: id=2, ...
    // Crt.2 - length(nume)>0: BVA cond 07: len=1, 08: len=0, 09: len=2, ...
    // Crt.3 - pret > 0:     BVA cond 13: pret=1, 14: pret=0, 15: pret=2, ...
    // =========================================================================

    @Test
    @Order(7)
    @Tag("BVA")
    @DisplayName("TC1_BVA Invalid - cond 02: id=0, pret=25 => ID invalid!")
    void testSave_BVA_Invalid_IdZero() {
        Product product = new Product(0, "Pumpkin-Spice latte", 25,
                CategorieBautura.ICED_COFFEE, TipBautura.BASIC);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validate(product),
                "Ar trebui sa arunce ValidationException pentru id=0");
        assertTrue(ex.getMessage().contains("ID invalid"),
                "Mesajul exceptiei trebuie sa contina 'ID invalid'");
    }

    @Test
    @Order(8)
    @Tag("BVA")
    @DisplayName("TC2_BVA Valid - cond 15: id=1, pret=2 => produs adaugat")
    void testSave_BVA_Valid_PretDoi() {
        Product product = new Product(1, "Pumpkin-Spice latte", 0.2,
                CategorieBautura.ICED_COFFEE, TipBautura.BASIC);

        validator.validate(product);
        Product saved = repository.save(product);

        assertNotNull(saved, "Produsul salvat nu trebuie sa fie null");
        assertEquals("Pumpkin-Spice latte", saved.getNume());
        assertEquals(2, saved.getPret(), 0.001);
        assertEquals(product, repository.findOne(1),
                "Produsul trebuie sa fie gasit in repository dupa save");
    }

    @Test
    @Order(9)
    @Tag("BVA")
    @DisplayName("TC3_BVA Valid - cond 09: id=1, nume='ab' (length=2), pret=25 => produs adaugat")
    void testSave_BVA_Valid_NumeScurt() {
        Product product = new Product(1, "ab", 25,
                CategorieBautura.ICED_COFFEE, TipBautura.BASIC);

        validator.validate(product);
        Product saved = repository.save(product);

        assertNotNull(saved, "Produsul salvat nu trebuie sa fie null");
        assertEquals("ab", saved.getNume());
        assertEquals(25, saved.getPret(), 0.001);
        assertEquals(product, repository.findOne(1),
                "Produsul trebuie sa fie gasit in repository dupa save");
    }

    @Test
    @Order(10)
    @Tag("BVA")
    @DisplayName("TC4_BVA Invalid - cond 14: id=1, pret=0 => Pret invalid!")
    void testSave_BVA_Invalid_PretZero() {
        Product product = new Product(1, "Pumpkin-Spice latte", 0,
                CategorieBautura.ICED_COFFEE, TipBautura.BASIC);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validate(product),
                "Ar trebui sa arunce ValidationException pentru pret=0");
        assertTrue(ex.getMessage().contains("Pret invalid"),
                "Mesajul exceptiei trebuie sa contina 'Pret invalid'");
    }

    @Test
    @Order(11)
    @Tag("BVA")
    @DisplayName("TC5_BVA Valid - boundary aproape de 0: id=7, nume='Matcha', pret=0.1 => produs adaugat")
    void testSave_BVA_Valid_Matcha_PretMinimPozitiv() {
        Product product = new Product(7, "Matcha", 0.1,
                CategorieBautura.SPECIAL_COFFEE, TipBautura.PLANT_BASED);

        validator.validate(product);
        Product saved = repository.save(product);

        assertNotNull(saved, "Produsul salvat nu trebuie sa fie null");
        assertEquals("Matcha", saved.getNume());
        assertEquals(0.1, saved.getPret(), 0.001);
        assertEquals(product, repository.findOne(7),
                "Produsul trebuie sa fie gasit in repository dupa save");
        assertEquals(1, repository.findAll().size(),
                "Repository-ul trebuie sa contina exact un produs");
    }

    @Test
    @Order(12)
    @Tag("BVA")
    @DisplayName("TC6_BVA Invalid - boundary: id=7, nume='Matcha', pret=0 => Pret invalid!")
    void testSave_BVA_Invalid_Matcha_PretZero() {
        Product product = new Product(7, "Matcha", 0,
                CategorieBautura.SPECIAL_COFFEE, TipBautura.PLANT_BASED);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validate(product),
                "Ar trebui sa arunce ValidationException pentru pret=0");
        assertTrue(ex.getMessage().contains("Pret invalid"),
                "Mesajul exceptiei trebuie sa contina 'Pret invalid'");
    }
}
