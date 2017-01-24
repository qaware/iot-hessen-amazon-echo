package de.qaware.alexa.warehouse.alexa;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import de.qaware.alexa.warehouse.business.Location;
import de.qaware.alexa.warehouse.business.UnknownWareException;
import de.qaware.alexa.warehouse.business.WarehouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Warehouse speechlet.
 */
@Component
public class WarehouseSpeechlet implements SpeechletV2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseSpeechlet.class);
    /**
     * If this flag is set in the session, the user wants a conversation and no one-shot intent.
     */
    private static final String SESSION_CONVERSATION_FLAG = "conversation";

    /**
     * String constants.
     */
    private static class Strings {
        static final String PROMPT_USER = "Was möchten Sie tun?";
        static final String WELCOME = "Willkommen beim Lager.";
        static final String GOODBYE = "Auf Wiedersehen!";
        static final String WARE_AMOUNT = "Es sind noch %d %s im Lager.";
        static final String WARE_ORDERED = "Ich habe %s nachbestellt.";
        static final String WARE_LOCATION = "%s befinden sich in Reihe %s, Regal %d, Fach %d";
        static final String MISSING_WARE = "Ich habe die Ware nicht verstanden.";
        static final String UNKNOWN_WARE = "Diese Ware kenne ich nicht.";

        /**
         * No instances allowed.
         */
        private Strings() {
        }
    }

    private final WarehouseService warehouseService;

    /**
     * Constructor.
     *
     * @param warehouseService Warehouse service.
     */
    @Autowired
    public WarehouseSpeechlet(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        LOGGER.info("onSessionStarted()");
    }

    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        LOGGER.info("onLaunch()");
        requestEnvelope.getSession().setAttribute(SESSION_CONVERSATION_FLAG, "true");
        return SpeechletResponse.newAskResponse(AlexaHelper.speech(Strings.WELCOME + " " + Strings.PROMPT_USER), AlexaHelper.repromt(Strings.PROMPT_USER));
    }

    @Override
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        LOGGER.info("onIntent()");

        Intent intent = requestEnvelope.getRequest().getIntent();
        switch (intent.getName()) {
            case "QueryInventory":
                return handleQueryInventory(requestEnvelope);
            case "OrderWare":
                return handleOrderWare(requestEnvelope);
            case "LocateWare":
                return handleLocateWare(requestEnvelope);
            case "Quit":
                return handleQuit();
            default:
                throw new IllegalArgumentException("Unknown intent: " + intent.getName());
        }
    }

    @Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        LOGGER.info("onSessionEnded()");
    }

    /**
     * Handles the Quit intent.
     *
     * @return Response.
     */
    private SpeechletResponse handleQuit() {
        return SpeechletResponse.newTellResponse(AlexaHelper.speech(Strings.GOODBYE));
    }

    /**
     * Handles the QueryInventory intent.
     *
     * @param requestEnvelope Request.
     * @return Response.
     */
    private SpeechletResponse handleQueryInventory(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        Optional<String> ware = AlexaHelper.getSlotValue(requestEnvelope.getRequest().getIntent(), "ware");
        LOGGER.debug("handleQueryInventory({})", ware);
        if (!ware.isPresent()) {
            return createMissingWareResponse(requestEnvelope);
        }

        try {
            int amount = warehouseService.getAmount(normalizeWare(ware.get()));

            if (isConversation(requestEnvelope.getSession())) {
                return SpeechletResponse.newAskResponse(AlexaHelper.speech(
                        String.format(Strings.WARE_AMOUNT, amount, ware.get()) + " " + Strings.PROMPT_USER),
                        AlexaHelper.repromt(Strings.PROMPT_USER)
                );
            } else {
                return SpeechletResponse.newTellResponse(AlexaHelper.speech(
                        String.format(Strings.WARE_AMOUNT, amount, ware.get())
                ));
            }
        } catch (UnknownWareException e) {
            return createUnknownWareResponse(requestEnvelope);
        }
    }

    /**
     * Handles the OrderWare intent.
     *
     * @param requestEnvelope Request.
     * @return Response.
     */
    private SpeechletResponse handleOrderWare(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        Optional<String> ware = AlexaHelper.getSlotValue(requestEnvelope.getRequest().getIntent(), "ware");
        LOGGER.debug("handleOrderWare({})", ware);
        if (!ware.isPresent()) {
            return createMissingWareResponse(requestEnvelope);
        }

        try {
            warehouseService.orderWare(normalizeWare(ware.get()));
            if (isConversation(requestEnvelope.getSession())) {
                return SpeechletResponse.newAskResponse(AlexaHelper.speech(
                        String.format(Strings.WARE_ORDERED, ware.get()) + " " + Strings.PROMPT_USER),
                        AlexaHelper.repromt(Strings.PROMPT_USER)
                );
            } else {
                return SpeechletResponse.newTellResponse(AlexaHelper.speech(
                        String.format(Strings.WARE_ORDERED, ware.get()))
                );
            }
        } catch (UnknownWareException e) {
            return createUnknownWareResponse(requestEnvelope);
        }
    }

    /**
     * Handles the LocateWare intent.
     *
     * @param requestEnvelope Request.
     * @return Response.
     */
    private SpeechletResponse handleLocateWare(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        Optional<String> ware = AlexaHelper.getSlotValue(requestEnvelope.getRequest().getIntent(), "ware");
        LOGGER.debug("handleLocateWare({})", ware);
        if (!ware.isPresent()) {
            return createMissingWareResponse(requestEnvelope);
        }

        try {
            Location location = warehouseService.locateWare(normalizeWare(ware.get()));
            if (isConversation(requestEnvelope.getSession())) {
                return SpeechletResponse.newAskResponse(AlexaHelper.speechSsml(
                        String.format("<speak>" + Strings.WARE_LOCATION + "<break strength=\"medium\"/>" + Strings.PROMPT_USER + "</speak>", ware.get(), location.getRow(), location.getRegal(), location.getShelf())),
                        AlexaHelper.repromt(Strings.PROMPT_USER)
                );
            } else {
                return SpeechletResponse.newTellResponse(
                        AlexaHelper.speech(String.format(Strings.WARE_LOCATION, ware.get(), location.getRow(), location.getRegal(), location.getShelf()))
                );
            }
        } catch (UnknownWareException e) {
            return createUnknownWareResponse(requestEnvelope);
        }
    }

    /**
     * Creates the response if the ware is missing in the request.
     *
     * @param requestEnvelope Request.
     * @return Response.
     */
    private SpeechletResponse createMissingWareResponse(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        if (isConversation(requestEnvelope.getSession())) {
            return SpeechletResponse.newAskResponse(AlexaHelper.speech(
                    Strings.MISSING_WARE + " " + Strings.PROMPT_USER), AlexaHelper.repromt(Strings.PROMPT_USER)
            );
        } else {
            return SpeechletResponse.newTellResponse(AlexaHelper.speech(
                    Strings.MISSING_WARE
            ));
        }
    }

    /**
     * Creates the response if the ware in the request is unknown.
     *
     * @param requestEnvelope Request.
     * @return Response.
     */
    private SpeechletResponse createUnknownWareResponse(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        if (isConversation(requestEnvelope.getSession())) {
            return SpeechletResponse.newAskResponse(AlexaHelper.speech(
                    Strings.UNKNOWN_WARE + " " + Strings.PROMPT_USER),
                    AlexaHelper.repromt(Strings.PROMPT_USER)
            );
        } else {
            return SpeechletResponse.newTellResponse(AlexaHelper.speech(
                    Strings.UNKNOWN_WARE
            ));
        }
    }

    /**
     * Determines if the user wants a conversation or a one-shot intent.
     *
     * @param session Session.
     * @return True if the user wants a conversation. False if the user issued a one-shot intent.
     */
    private boolean isConversation(Session session) {
        return session.getAttribute(SESSION_CONVERSATION_FLAG) != null;
    }

    /**
     * Normalizes the value of the ware slot.
     *
     * @param ware Ware.
     * @return Normalized ware.
     */
    private String normalizeWare(String ware) {
        return ware.toLowerCase();
    }
}
