package codegenerator;

import log.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohammad hosein on 6/27/2015.
 */
public class Memory {
    private List<ThreeAddressCode> codeBlock = new ArrayList<>();
    private int lastTempIndex;
    private int lastDataAddress;
    private final static int START_TEMP_MEMORY_ACCESS = 500;
    private final static int START_DATA_MEMORY_ACCESS = 200;
    private final static int DATA_SIZE = 4;
    private final static int TEMP_SIZE = 4;

    public Memory() {
        lastTempIndex = START_TEMP_MEMORY_ACCESS;
        lastDataAddress = START_DATA_MEMORY_ACCESS;
    }

    public int getTemp() {
        lastTempIndex += TEMP_SIZE;
        return lastTempIndex - TEMP_SIZE;
    }
    public  int getDateAddress(){
        lastDataAddress += DATA_SIZE;
        return lastDataAddress- DATA_SIZE;
    }
    public int saveMemory() {
        codeBlock.add(new ThreeAddressCode());
        return codeBlock.size() - 1;
    }

    public void addThreeAddressCode(Operation op, Address opr1, Address opr2, Address opr3) {
        codeBlock.add(new ThreeAddressCode(op,opr1,opr2,opr3));
    }

    public void addThreeAddressCode(int i, Operation op, Address opr1, Address opr2, Address opr3) {
        codeBlock.remove(i);
        codeBlock.add(i, new ThreeAddressCode(op, opr1, opr2,opr3));
    }


    public int getCurrentCodeBlockAddress() {
        return codeBlock.size();
    }

    public void pintCodeBlock() {
        Log.print("Code Block");
        for (int i = 0; i < codeBlock.size(); i++) {
            Log.print(i + " : " + codeBlock.get(i).toString());
        }
    }
}
