package root.app.controllers;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import root.app.model.AppConfigDTO;
import root.app.properties.AppConfigService;
import root.app.properties.ConfigAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SettingsControllerTest {

    @Mock
    private AppConfigService appConfigService;

    @InjectMocks
    private SettingsController controller;
    private List<AppConfigDTO> dtos;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }


    @Test(expected = IllegalArgumentException.class)
    public void emptyRequestIsNotValid() throws Exception {
        //WHEN
        controller.updateProperty(Collections.emptyList());

        //THEN
        //exception
    }

    @Test
    public void serviceCalledForEachDtoOnes() throws Exception {
        final List<AppConfigDTO> dtos = getDtos();

        //WHEN
        controller.updateProperty(getDtos());

        //THEN
        verify(appConfigService).save(dtos.get(0));
        verify(appConfigService).save(dtos.get(1));
    }

    @Test
    public void controllerCallsServiceForProps() throws Exception {
        //GIVEN
        final List<AppConfigDTO> dtos = getDtos();
        when(appConfigService.findAll()).thenReturn(dtos);
        //WHEN
        final List<AppConfigDTO> appConfigDTOS = controller.loadProps();

        //THEN
        assertEquals(appConfigDTOS, dtos);
    }

    private List<AppConfigDTO> getDtos() {
        AppConfigDTO appConfigDTO1 = new AppConfigDTO(ConfigAttribute.ZonesPerLineAmount, "2");
        AppConfigDTO appConfigDTO2 = new AppConfigDTO(ConfigAttribute.CameraIP, "IP");
        return Lists.newArrayList(appConfigDTO1, appConfigDTO2);
    }
}