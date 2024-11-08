package org.example.formulaeditor.parser.ast;

import org.example.formulaeditor.parser.ASTNodeVisitor;

public interface ASTNode {
  public <T> T accept(ASTNodeVisitor<T> visitor);
}
