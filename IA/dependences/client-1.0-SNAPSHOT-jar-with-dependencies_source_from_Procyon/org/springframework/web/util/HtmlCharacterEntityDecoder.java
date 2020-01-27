// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.util;

class HtmlCharacterEntityDecoder
{
    private static final int MAX_REFERENCE_SIZE = 10;
    private final HtmlCharacterEntityReferences characterEntityReferences;
    private final String originalMessage;
    private final StringBuilder decodedMessage;
    private int currentPosition;
    private int nextPotentialReferencePosition;
    private int nextSemicolonPosition;
    
    public HtmlCharacterEntityDecoder(final HtmlCharacterEntityReferences characterEntityReferences, final String original) {
        this.currentPosition = 0;
        this.nextPotentialReferencePosition = -1;
        this.nextSemicolonPosition = -2;
        this.characterEntityReferences = characterEntityReferences;
        this.originalMessage = original;
        this.decodedMessage = new StringBuilder(this.originalMessage.length());
    }
    
    public String decode() {
        while (this.currentPosition < this.originalMessage.length()) {
            this.findNextPotentialReference(this.currentPosition);
            this.copyCharactersTillPotentialReference();
            this.processPossibleReference();
        }
        return this.decodedMessage.toString();
    }
    
    private void findNextPotentialReference(final int startPosition) {
        this.nextPotentialReferencePosition = Math.max(startPosition, this.nextSemicolonPosition - 10);
        do {
            this.nextPotentialReferencePosition = this.originalMessage.indexOf(38, this.nextPotentialReferencePosition);
            if (this.nextSemicolonPosition != -1 && this.nextSemicolonPosition < this.nextPotentialReferencePosition) {
                this.nextSemicolonPosition = this.originalMessage.indexOf(59, this.nextPotentialReferencePosition + 1);
            }
            final boolean isPotentialReference = this.nextPotentialReferencePosition != -1 && this.nextSemicolonPosition != -1 && this.nextPotentialReferencePosition - this.nextSemicolonPosition < 10;
            if (isPotentialReference) {
                break;
            }
            if (this.nextPotentialReferencePosition == -1) {
                break;
            }
            if (this.nextSemicolonPosition == -1) {
                this.nextPotentialReferencePosition = -1;
                break;
            }
            ++this.nextPotentialReferencePosition;
        } while (this.nextPotentialReferencePosition != -1);
    }
    
    private void copyCharactersTillPotentialReference() {
        if (this.nextPotentialReferencePosition != this.currentPosition) {
            final int skipUntilIndex = (this.nextPotentialReferencePosition != -1) ? this.nextPotentialReferencePosition : this.originalMessage.length();
            if (skipUntilIndex - this.currentPosition > 3) {
                this.decodedMessage.append(this.originalMessage.substring(this.currentPosition, skipUntilIndex));
                this.currentPosition = skipUntilIndex;
            }
            else {
                while (this.currentPosition < skipUntilIndex) {
                    this.decodedMessage.append(this.originalMessage.charAt(this.currentPosition++));
                }
            }
        }
    }
    
    private void processPossibleReference() {
        if (this.nextPotentialReferencePosition != -1) {
            final boolean isNumberedReference = this.originalMessage.charAt(this.currentPosition + 1) == '#';
            final boolean wasProcessable = isNumberedReference ? this.processNumberedReference() : this.processNamedReference();
            if (wasProcessable) {
                this.currentPosition = this.nextSemicolonPosition + 1;
            }
            else {
                final char currentChar = this.originalMessage.charAt(this.currentPosition);
                this.decodedMessage.append(currentChar);
                ++this.currentPosition;
            }
        }
    }
    
    private boolean processNumberedReference() {
        final boolean isHexNumberedReference = this.originalMessage.charAt(this.nextPotentialReferencePosition + 2) == 'x' || this.originalMessage.charAt(this.nextPotentialReferencePosition + 2) == 'X';
        try {
            final int value = isHexNumberedReference ? Integer.parseInt(this.getReferenceSubstring(3), 16) : Integer.parseInt(this.getReferenceSubstring(2));
            this.decodedMessage.append((char)value);
            return true;
        }
        catch (NumberFormatException ex) {
            return false;
        }
    }
    
    private boolean processNamedReference() {
        final String referenceName = this.getReferenceSubstring(1);
        final char mappedCharacter = this.characterEntityReferences.convertToCharacter(referenceName);
        if (mappedCharacter != '\uffff') {
            this.decodedMessage.append(mappedCharacter);
            return true;
        }
        return false;
    }
    
    private String getReferenceSubstring(final int referenceOffset) {
        return this.originalMessage.substring(this.nextPotentialReferencePosition + referenceOffset, this.nextSemicolonPosition);
    }
}
