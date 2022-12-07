package riotgamesdiscordbot.tournament.validations;

/**
 * Validates one specific thing. Not intended to validate the whole.
 */
public interface Validation {
    ValidationStatus validate();
}
