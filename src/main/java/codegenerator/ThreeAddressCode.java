package codegenerator;

public class ThreeAddressCode {
    public Operation operation;
    public Address Operand1;
    public Address Operand2;
    public Address Operand3;

    public ThreeAddressCode() {

    }

    public ThreeAddressCode(Operation op, Address opr1, Address opr2, Address opr3) {
        operation = op;
        Operand1 = opr1;
        Operand2 = opr2;
        Operand3 = opr3;
    }

    public String toString()
    {
        if(operation == null) return "";
        StringBuffer res = new StringBuffer("(");
        res.append(operation.toString()).append(",");
        if(Operand1 != null)
            res.append(Operand1.toString());
        res.append(",");
        if(Operand2 != null)
            res.append(Operand2.toString());
        res.append(",");
        if(Operand3 != null)
            res.append(Operand3.toString());
        res.append(")");

        return res.toString();
    }
}
