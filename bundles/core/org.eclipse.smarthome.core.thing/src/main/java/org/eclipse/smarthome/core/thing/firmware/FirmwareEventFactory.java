/**
 * Copyright (c) 2014,2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.smarthome.core.thing.firmware;

import org.eclipse.smarthome.core.events.AbstractEventFactory;
import org.eclipse.smarthome.core.events.Event;
import org.eclipse.smarthome.core.events.EventFactory;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.osgi.service.component.annotations.Component;

import com.google.common.collect.ImmutableSet;

/**
 * The {@link FirmwareEventFactory} is registered as an OSGi service and is responsible to create firmware events. It
 * supports the following event types:
 * <ul>
 * <li>{@link FirmwareStatusInfoEvent#TYPE}</li>
 * <li>{@link FirmwareUpdateProgressInfoEvent#TYPE}</li>
 * <li>{@link FirmwareUpdateResultInfoEvent#TYPE}</li>
 * </ul>
 *
 * @author Thomas Höfer - Initial contribution
 */
@Component(immediate = true, service = EventFactory.class)
public final class FirmwareEventFactory extends AbstractEventFactory {

    private static final int THING_UID_TOPIC_IDX = 2;

    static final String THING_UID_TOPIC_KEY = "{thingUID}";

    static final String FIRMWARE_STATUS_TOPIC = "smarthome/things/{thingUID}/firmware/status";
    static final String FIRMWARE_UPDATE_PROGRESS_TOPIC = "smarthome/things/{thingUID}/firmware/update/progress";
    static final String FIRMWARE_UPDATE_RESULT_TOPIC = "smarthome/things/{thingUID}/firmware/update/result";

    /**
     * Creates a new firmware event factory.
     */
    public FirmwareEventFactory() {
        super(ImmutableSet.of(FirmwareStatusInfoEvent.TYPE, FirmwareUpdateProgressInfoEvent.TYPE,
                FirmwareUpdateResultInfoEvent.TYPE));
    }

    @Override
    protected Event createEventByType(String eventType, String topic, String payload, String source) throws Exception {
        if (FirmwareStatusInfoEvent.TYPE.equals(eventType)) {
            return createFirmwareStatusInfoEvent(topic, payload);
        } else if (FirmwareUpdateProgressInfoEvent.TYPE.equals(eventType)) {
            return createFirmwareUpdateProgressInfoEvent(topic, payload);
        } else if (FirmwareUpdateResultInfoEvent.TYPE.equals(eventType)) {
            return createFirmwareUpdateResultInfoEvent(topic, payload);
        }
        return null;
    }

    /**
     * Creates a new {@link FirmwareStatusInfoEvent}.
     *
     * @param firmwareStatusInfo the firmware status information (must not be null)
     * @param thingUID the thing UID for which the new firmware status info is to be sent (must not be null)
     *
     * @return the corresponding firmware status info event
     *
     * @throws NullPointerException if given firmware status info or thing UID is null
     */
    static FirmwareStatusInfoEvent createFirmwareStatusInfoEvent(FirmwareStatusInfo firmwareStatusInfo,
            ThingUID thingUID) {
        checkNotNull(firmwareStatusInfo, "firmwareStatusInfo");
        checkNotNull(thingUID, "thingUID");

        String topic = FIRMWARE_STATUS_TOPIC.replace(THING_UID_TOPIC_KEY, thingUID.getAsString());
        String payload = serializePayload(firmwareStatusInfo);

        return new FirmwareStatusInfoEvent(topic, payload, firmwareStatusInfo, thingUID);
    }

    /**
     * Creates a new {@link FirmwareUpdateProgressInfoEvent}.
     *
     * @param progressInfo the progress information of the firmware update process (must not be null)
     * @param thingUID the thing UID for which the progress info is to be sent (must not be null)
     *
     * @return the corresponding progress info event
     *
     * @throws NullPointerException if given progress info or thing UID is null
     */
    static FirmwareUpdateProgressInfoEvent createFirmwareUpdateProgressInfoEvent(
            FirmwareUpdateProgressInfo progressInfo, ThingUID thingUID) {
        checkNotNull(progressInfo, "progressInfo");
        checkNotNull(thingUID, "thingUID");

        String topic = FIRMWARE_UPDATE_PROGRESS_TOPIC.replace(THING_UID_TOPIC_KEY, thingUID.getAsString());
        String payload = serializePayload(progressInfo);

        return new FirmwareUpdateProgressInfoEvent(topic, payload, progressInfo, thingUID);
    }

    /**
     * Creates a new {@link FirmwareUpdateResultInfoEvent}.
     *
     * @param firmwareUpdateResultInfo the firmware update result information (must not be null)
     * @param thingUID the thing UID for which the result information is to be sent (must not be null)
     *
     * @return the corresponding firmware update result info event
     *
     * @throws NullPointerException if given firmware update result info event or thing UID is null
     */
    static FirmwareUpdateResultInfoEvent createFirmwareUpdateResultInfoEvent(
            FirmwareUpdateResultInfo firmwareUpdateResultInfo, ThingUID thingUID) {
        checkNotNull(firmwareUpdateResultInfo, "firmwareUpdateResultInfo");
        checkNotNull(thingUID, "thingUID");

        String topic = FIRMWARE_UPDATE_RESULT_TOPIC.replace(THING_UID_TOPIC_KEY, thingUID.getAsString());
        String payload = serializePayload(firmwareUpdateResultInfo);

        return new FirmwareUpdateResultInfoEvent(topic, payload, firmwareUpdateResultInfo, thingUID);
    }

    private static FirmwareStatusInfoEvent createFirmwareStatusInfoEvent(String topic, String payload) {
        FirmwareStatusInfo firmwareStatusInfo = deserializePayload(payload, FirmwareStatusInfo.class);
        return new FirmwareStatusInfoEvent(topic, payload, firmwareStatusInfo, getThingUID(topic));
    }

    private static FirmwareUpdateProgressInfoEvent createFirmwareUpdateProgressInfoEvent(String topic, String payload) {
        FirmwareUpdateProgressInfo firmwareUpdateProgressInfo = deserializePayload(payload,
                FirmwareUpdateProgressInfo.class);
        return new FirmwareUpdateProgressInfoEvent(topic, payload, firmwareUpdateProgressInfo, getThingUID(topic));
    }

    private static FirmwareUpdateResultInfoEvent createFirmwareUpdateResultInfoEvent(String topic, String payload) {
        FirmwareUpdateResultInfo firmwareUpdateResultInfo = deserializePayload(payload, FirmwareUpdateResultInfo.class);
        return new FirmwareUpdateResultInfoEvent(topic, payload, firmwareUpdateResultInfo, getThingUID(topic));
    }

    private static ThingUID getThingUID(String topic) {
        String thingUID = topic.split("/")[THING_UID_TOPIC_IDX];
        return new ThingUID(thingUID);
    }
}
