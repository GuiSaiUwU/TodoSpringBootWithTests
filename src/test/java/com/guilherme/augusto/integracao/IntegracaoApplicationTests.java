package com.guilherme.augusto.integracao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.guilherme.augusto.integracao.entidades.Todo;
import com.guilherme.augusto.integracao.repository.TodoRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
class IntegracaoApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TodoRepository todoRepository;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("http://localhost:" + port + "/tarefas");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("todoForm")));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void deveInserirTarefaPelaInterfaceEConfirmarNoBanco() {
        preencherFormulario("Tarefa criada", "Descrição criada");
        driver.findElement(By.id("submitButton")).click();

        Todo todo = waitForTodo("Tarefa criada");
        assertEquals("Descrição criada", todo.getDescricao());
    }

    @Test
    void deveAtualizarTarefaPelaInterfaceEConfirmarNoBanco() {
        Todo existente = todoRepository.save(criarTodo("Título original", "Descrição original"));
        driver.navigate().refresh();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".todo-item button.btn-edit"))).click();
        preencherFormulario("Título atualizado", "Descrição atualizada");
        driver.findElement(By.id("submitButton")).click();

        Todo atualizado = waitForTodo("Título atualizado");
        assertEquals(existente.getId(), atualizado.getId());
        assertEquals("Descrição atualizada", atualizado.getDescricao());
    }

    @Test
    void deveExcluirTarefaPelaInterfaceEConfirmarNoBanco() {
        Todo existente = todoRepository.save(criarTodo("Tarefa para excluir", "Descrição"));
        driver.navigate().refresh();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".todo-item button.btn-delete"))).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".todo-item")));

        assertFalse(todoRepository.existsById(existente.getId()));
    }

    private void preencherFormulario(String titulo, String descricao) {
        driver.findElement(By.id("titulo")).clear();
        driver.findElement(By.id("titulo")).sendKeys(titulo);
        driver.findElement(By.id("descricao")).clear();
        driver.findElement(By.id("descricao")).sendKeys(descricao);
    }

    private Todo waitForTodo(String titulo) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("todoList"), titulo));
        return todoRepository.findAll().stream()
                .filter(todo -> titulo.equals(todo.getTitulo()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Tarefa não encontrada no banco: " + titulo));
    }

    private Todo criarTodo(String titulo, String descricao) {
        Todo todo = new Todo();
        todo.setTitulo(titulo);
        todo.setDescricao(descricao);
        return todo;
    }
}
