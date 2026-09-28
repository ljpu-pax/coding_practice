/*
============ file (protobuf) ============
message MyObject {
    int32 a,
    double b,
    bool c,
    MyObject2 d
}

message MyObject2 {
    int32 x,
    double y
}
============ file ============

Only primitive: bool (1 byte), int32 (4 bytes), double (8 bytes)

Question: int getSize(String objectTypeName)
getSize("double") -> 8
getSize("MyObject2") -> 12 = 4 + 8
getSize("MyObject") -> 25 = 4 + 8 + 1 + (12)
*/

import java.util.*;

class ProtobufSizeCalculator {
    // Map to store message definitions: messageName -> list of field types
    private Map<String, List<String>> messageDefinitions;

    // Map to store primitive type sizes
    private Map<String, Integer> primitiveSizes;

    // Memoization cache
    private Map<String, Integer> sizeCache;

    public ProtobufSizeCalculator(String protobufDefinition) {
        messageDefinitions = new HashMap<>();
        primitiveSizes = new HashMap<>();
        sizeCache = new HashMap<>();

        // Initialize primitive sizes
        primitiveSizes.put("bool", 1);
        primitiveSizes.put("int32", 4);
        primitiveSizes.put("double", 8);

        // Parse the protobuf definition
        parseProtobufDefinition(protobufDefinition);
    }

    private void parseProtobufDefinition(String definition) {
        String[] lines = definition.split("\n");
        String currentMessage = null;
        List<String> currentFields = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();

            // Check if this is a message declaration
            if (line.startsWith("message ")) {
                // Save previous message if exists
                if (currentMessage != null) {
                    messageDefinitions.put(currentMessage, new ArrayList<>(currentFields));
                }

                // Extract message name
                currentMessage = line.substring(8, line.indexOf("{")).trim();
                currentFields.clear();
            }
            // Check if this is a field declaration
            else if (currentMessage != null && !line.isEmpty() && !line.equals("}")) {
                // Extract field type (first word before space)
                String[] parts = line.split("\\s+");
                if (parts.length > 0) {
                    String fieldType = parts[0];
                    // Remove trailing comma if exists
                    fieldType = fieldType.replace(",", "");
                    if (!fieldType.isEmpty()) {
                        currentFields.add(fieldType);
                    }
                }
            }
            // Check if message ends
            else if (line.equals("}") && currentMessage != null) {
                messageDefinitions.put(currentMessage, new ArrayList<>(currentFields));
                currentMessage = null;
                currentFields.clear();
            }
        }
    }

    public int getSize(String objectTypeName) {
        // Check cache first
        if (sizeCache.containsKey(objectTypeName)) {
            return sizeCache.get(objectTypeName);
        }

        // If it's a primitive type
        if (primitiveSizes.containsKey(objectTypeName)) {
            return primitiveSizes.get(objectTypeName);
        }

        // If it's a message type
        if (messageDefinitions.containsKey(objectTypeName)) {
            int totalSize = 0;
            for (String fieldType : messageDefinitions.get(objectTypeName)) {
                totalSize += getSize(fieldType); // Recursive call
            }
            sizeCache.put(objectTypeName, totalSize);
            return totalSize;
        }

        throw new IllegalArgumentException("Unknown type: " + objectTypeName);
    }

    public static void main(String[] args) {
        String protobufDef =
            "message MyObject {\n" +
            "    int32 a,\n" +
            "    double b,\n" +
            "    bool c,\n" +
            "    MyObject2 d\n" +
            "}\n" +
            "\n" +
            "message MyObject2 {\n" +
            "    int32 x,\n" +
            "    double y\n" +
            "}";

        ProtobufSizeCalculator calc = new ProtobufSizeCalculator(protobufDef);

        System.out.println(calc.getSize("double"));      // 8
        System.out.println(calc.getSize("MyObject2"));   // 12
        System.out.println(calc.getSize("MyObject"));    // 25
    }
}
