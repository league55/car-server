package root.app.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

public class HomeControllerTest {

    private HomeController homeController = new HomeController();

    @Test
    public void homeReturnsRightFile() throws Exception {
        //WHEN
        final String result = homeController.home();

        //THEN
        assertEquals(result, "index.html");
    }

}