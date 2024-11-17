package org.example.formulaeditor.parser.ast;

public enum BasicFunction {
    NOT, IF, AND, OR, MIN, MAX, SUM, ABS, PRODUCT, MEAN;

    public int getPriority() {
        return switch (this) {
            case IF -> 10;
            case AND, OR -> 8;
            case MIN, MAX -> 6;
            case SUM, PRODUCT -> 5;
            case MEAN -> 4;
            case ABS -> 3;
            case NOT -> 1;
        };
    }
}