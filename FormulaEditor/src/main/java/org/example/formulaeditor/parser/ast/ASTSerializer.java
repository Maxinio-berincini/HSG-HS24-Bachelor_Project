package org.example.formulaeditor.parser.ast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ASTSerializer {

    private ASTSerializer() {
    }

    // Convert ASTNode with children to json
    public static String astToJsonString(ASTNode node) {
        JsonObject jsonObj = astToJson(node);
        return jsonObj.toString();
    }

    public static JsonObject astToJson(ASTNode node) {
        if (node == null) {
            return new JsonObject();
        }

        AbstractASTNode absNode = (AbstractASTNode) node;

        // Create json object
        JsonObject obj = new JsonObject();
        obj.addProperty("revisionCount", absNode.getRevisionCount());

        // Check node type
        if (node instanceof Binary bin) {
            obj.addProperty("type", "Binary");

            obj.addProperty("op", bin.op.name());

            obj.add("left", astToJson(bin.left));
            obj.add("right", astToJson(bin.right));

        } else if (node instanceof Number<?> num) {
            obj.addProperty("type", "Number");
            obj.addProperty("value", num.value.toString());

        } else if (node instanceof ExcelString str) {
            obj.addProperty("type", "ExcelString");
            obj.addProperty("value", str.value);

        } else if (node instanceof Boolean bool) {
            obj.addProperty("type", "Boolean");
            obj.addProperty("value", bool.value);

        } else if (node instanceof Cell cell) {
            obj.addProperty("type", "Cell");
            obj.addProperty("column", cell.column);
            obj.addProperty("row", cell.row);

        } else if (node instanceof CellRange range) {
            obj.addProperty("type", "CellRange");
            obj.add("start", astToJson(range.start));
            obj.add("end", astToJson(range.end));

        } else if (node instanceof Negate neg) {
            obj.addProperty("type", "Negate");
            obj.add("innerNode", astToJson(neg.node));

        } else if (node instanceof FunctionCall funCall) {
            obj.addProperty("type", "FunctionCall");
            obj.addProperty("functionName", funCall.functionName.name());

            JsonArray argsArr = new JsonArray();
            for (ASTNode arg : funCall.args) {
                argsArr.add(astToJson(arg));
            }
            obj.add("args", argsArr);

        } else {
            // In case of malformed node type
            obj.addProperty("type", "Unknown");
        }

        return obj;
    }

    public static ASTNode astFromJsonString(String jsonString) {
        if (jsonString == null || jsonString.isBlank()) {
            return null;
        }
        JsonElement jsonElement = JsonParser.parseString(jsonString);
        if (!jsonElement.isJsonObject()) {
            return null;
        }
        return astFromJson(jsonElement.getAsJsonObject());
    }

    public static ASTNode astFromJson(JsonObject obj) {
        if (obj == null || obj.keySet().isEmpty()) {
            return null;
        }

        // Get revision count
        int revisionCount = 1;
        if (obj.has("revisionCount")) {
            revisionCount = obj.get("revisionCount").getAsInt();
        }

        // Get node type
        String type = obj.has("type") ? obj.get("type").getAsString() : "Unknown";

        switch (type) {
            case "Binary" -> {
                BinaryOp op = BinaryOp.valueOf(obj.get("op").getAsString());
                ASTNode left = astFromJson(obj.get("left").getAsJsonObject());
                ASTNode right = astFromJson(obj.get("right").getAsJsonObject());
                Binary binary = new Binary(left, op, right);
                binary.setRevisionCount(revisionCount);
                return binary;
            }
            case "Number" -> {
                String valStr = obj.get("value").getAsString();
                Number<?> numNode = parseNumber(valStr);
                numNode.setRevisionCount(revisionCount);
                return numNode;
            }
            case "ExcelString" -> {
                String val = obj.get("value").getAsString();
                ExcelString str = new ExcelString(val);
                str.setRevisionCount(revisionCount);
                return str;
            }
            case "Boolean" -> {
                boolean boolVal = obj.get("value").getAsBoolean();
                org.example.formulaeditor.parser.ast.Boolean boolNode
                        = new org.example.formulaeditor.parser.ast.Boolean(boolVal);
                boolNode.setRevisionCount(revisionCount);
                return boolNode;
            }
            case "Cell" -> {
                String col = obj.get("column").getAsString();
                int row = obj.get("row").getAsInt();
                Cell cell = new Cell(col, row);
                cell.setRevisionCount(revisionCount);
                return cell;
            }
            case "CellRange" -> {
                JsonObject startObj = obj.getAsJsonObject("start");
                JsonObject endObj = obj.getAsJsonObject("end");
                Cell startCell = (Cell) astFromJson(startObj);
                Cell endCell = (Cell) astFromJson(endObj);
                CellRange cellRange = new CellRange(startCell, endCell);
                cellRange.setRevisionCount(revisionCount);
                return cellRange;
            }
            case "Negate" -> {
                JsonObject innerObj = obj.getAsJsonObject("innerNode");
                ASTNode inner = astFromJson(innerObj);
                Negate neg = new Negate(inner);
                neg.setRevisionCount(revisionCount);
                return neg;
            }
            case "FunctionCall" -> {
                String funcName = obj.get("functionName").getAsString();
                BasicFunction basicFunc = BasicFunction.valueOf(funcName);

                JsonArray argsArr = obj.getAsJsonArray("args");
                java.util.ArrayList<ASTNode> argList = new java.util.ArrayList<>();
                for (JsonElement e : argsArr) {
                    JsonObject argObj = e.getAsJsonObject();
                    argList.add(astFromJson(argObj));
                }
                FunctionCall funcCall = new FunctionCall(basicFunc, argList);
                funcCall.setRevisionCount(revisionCount);
                return funcCall;
            }
            default -> {
                return null;
            }
        }
    }

    private static Number<?> parseNumber(String valueStr) {
        // Try int
        try {
            long l = Long.parseLong(valueStr);
            return new org.example.formulaeditor.parser.ast.Number<>(l);
        } catch (NumberFormatException ignored) {
        }

        // Try double
        try {
            double d = Double.parseDouble(valueStr);
            return new org.example.formulaeditor.parser.ast.Number<>(d);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unable to parse number: " + valueStr, e);
        }
    }
}
