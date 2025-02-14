package org.example.formulaeditor.parser.ast;

import org.example.formulaeditor.parser.ASTNodeVisitor;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionCall extends AbstractASTNode {
    public final BasicFunction functionName;
    public final List<ASTNode> args;

    public FunctionCall(BasicFunction name, List<ASTNode> args) {
        super(7);
        this.functionName = name;
        this.args = args;
    }

    public <T> T accept(ASTNodeVisitor<T> visitor) {
        return visitor.visitFunctionCall(this);
    }

    public String toString() {
        return this.functionName + "(" + args.stream().map(a -> a.toString()).collect(Collectors.joining(", ")) + ")";
    }
}

