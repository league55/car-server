package root.app.controllers;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import root.app.controllers.request.GridUpdateRequest;
import root.app.data.services.CalibrationService;

import javax.xml.ws.soap.MTOM;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static root.app.properties.ConfigAttribute.ZonesPerLineAmount;

@RunWith(JUnitParamsRunner.class)
public class GridCalibrationControllerTest {

    @Mock
    private CalibrationService calibrationService;

    @InjectMocks
    private GridCalibrationController controller;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void requestWithWrongCalibrationTypesNotValid() throws Exception {
        //WHEN
        final ResponseEntity result = controller.updateGrid(new GridUpdateRequest("NoSuchType", 0.0, 0));

        //THEN
        assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST);
        verify(calibrationService, never()).fixPosition(any(), any());
    }

    @Test
    @Parameters({"ZonesPerLineAmount", "RoadWaysAmount"})
    public void statusIsOkWithValidCalibrType(String calibrationType) throws Exception {
        //WHEN
        final ResponseEntity result = controller.updateGrid(new GridUpdateRequest(calibrationType, 0.0, 0));

        //THEN
        assertEquals(result.getStatusCode(), HttpStatus.OK);
        verify(calibrationService).fixPosition(any(), any());
    }

}