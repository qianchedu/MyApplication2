package com.powerful.activity.upnp;


import com.powerful.activity.service.BeyondUpnpService;
import com.powerful.activity.service.SystemService;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.registry.Registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SystemManager {
    private static final String TAG = SystemManager.class.getSimpleName();
    public static final ServiceType CONTENT_DIRECTORY_SERVICE = new UDAServiceType("ContentDirectory");
    public static final ServiceType AV_TRANSPORT_SERVICE = new UDAServiceType("AVTransport");
    public static final ServiceType RENDERING_CONTROL_SERVICE = new UDAServiceType("RenderingControl");
    private DeviceType dmrDeviceType = new UDADeviceType("MediaRenderer");

    private static SystemManager INSTANCE = null;
    //Service
    private BeyondUpnpService mUpnpService;
    private SystemService mSystemService;

    private SystemManager() {
    }

    public static SystemManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SystemManager();
        }
        return INSTANCE;
    }

    public void setUpnpService(BeyondUpnpService upnpService) {
        mUpnpService = upnpService;
    }

    public void setSystemService(SystemService systemService) {
        mSystemService = systemService;
    }

    public void searchAllDevices() {
        mUpnpService.getControlPoint().search();
    }

    public Collection<Device> getDmrDevices() {
        return mUpnpService.getRegistry().getDevices(dmrDeviceType);
    }

    public ControlPoint getControlPoint() {
        return mUpnpService.getControlPoint();
    }

    public Registry getRegistry() {
        return mUpnpService.getRegistry();
    }

    public Collection<Device> getDmcDevices() {
        if (mUpnpService == null) return Collections.EMPTY_LIST;

        List<Device> devices = new ArrayList<>();
        devices.addAll(mUpnpService.getRegistry().getDevices(CONTENT_DIRECTORY_SERVICE));
        return devices;
    }

    public Device getSelectedDevice() {
        return mSystemService.getSelectedDevice();
    }

    public void setSelectedDevice(Device selectedDevice) {
        mSystemService.setSelectedDevice(selectedDevice, mUpnpService.getControlPoint());
    }

    public int getDeviceVolume() {
        return mSystemService.getDeviceVolume();
    }

    public void setDeviceVolume(int currentVolume) {
        mSystemService.setDeviceVolume(currentVolume);
    }

    public void destroy(){

    }
}
