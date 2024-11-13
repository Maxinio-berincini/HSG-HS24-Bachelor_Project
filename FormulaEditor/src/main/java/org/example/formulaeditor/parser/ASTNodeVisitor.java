package org.example.formulaeditor.parser;

import org.example.formulaeditor.parser.ast.Boolean;
import org.example.formulaeditor.parser.ast.Number;
import org.example.formulaeditor.parser.ast.*;

public interface ASTNodeVisitor<T> {

    public <N extends java.lang.Number> T visitNumber(Number<N> n);

    public T visitCell(Cell n);

    public T visitCellRange(CellRange n);

    public T visitString(ExcelString n);

    public T visitFunctionCall(FunctionCall n);

    public T visitBoolean(Boolean n);

    public T visitBinary(Binary n);

    public T visitNegate(Negate n);

}
