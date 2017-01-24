package de.qaware.alexa.warehouse.alexa;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;

import java.util.Optional;

/**
 * Alexa helper functions.
 */
public final class AlexaHelper {
    /**
     * No instances allowed.
     */
    private AlexaHelper() {
    }

    /**
     * Returns the slot value with the given name.
     *
     * @param intent   Intent, which contains the slots.
     * @param slotName Slot name.
     * @return Optional containing the slot value. If the slot doesn't exist, {@link Optional#empty()} is returned.
     */
    public static Optional<String> getSlotValue(Intent intent, String slotName) {
        Slot slot = intent.getSlot(slotName);
        if (slot == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(slot.getValue());
    }

    /**
     * Creates a reprompt with the given question.
     *
     * @param question Question.
     * @return Reprompt.
     */
    public static Reprompt repromt(String question) {
        Reprompt result = new Reprompt();
        result.setOutputSpeech(speech(question));
        return result;
    }

    /**
     * Creates an output speech with the given text.
     *
     * @param text Text.
     * @return Output speech.
     */
    public static OutputSpeech speech(String text) {
        PlainTextOutputSpeech result = new PlainTextOutputSpeech();
        result.setText(text);
        return result;
    }

    /**
     * Creates an output speech with the given SSML formatted text.
     *
     * @param ssml Text.
     * @return Output speech.
     */
    public static OutputSpeech speechSsml(String ssml) {
        SsmlOutputSpeech result = new SsmlOutputSpeech();
        result.setSsml(ssml);
        return result;
    }
}
