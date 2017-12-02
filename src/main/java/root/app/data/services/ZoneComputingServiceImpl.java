package root.app.data.services;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import root.app.model.MarkersPair;
import root.app.model.Zone;

import java.util.List;

@Service
public class ZoneComputingServiceImpl implements ZoneComputingService {
    @Override
    public List<Zone> getChildZones(MarkersPair pair, int zoneAmount) {
        return Lists.newArrayList(new Zone());
    }
}
