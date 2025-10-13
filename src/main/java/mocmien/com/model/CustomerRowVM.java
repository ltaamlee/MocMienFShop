package mocmien.com.model;

import java.time.LocalDate;
import mocmien.com.entity.Customer;
import mocmien.com.enums.CustomerRank;

public record CustomerRowVM(
        Integer maKH,
        String hoTen,
        String sdt,
        String email,
        long tongDon,
        CustomerRank hang,
        LocalDate ngayTao
) {
    public static CustomerRowVM from(Customer c, String email, long tongDon, LocalDate firstOrder){
        return new CustomerRowVM(
                c.getMaKH(),
                c.getHoTen(),
                c.getSdt(),
                email,
                tongDon,
                CustomerRank.ofTotalOrders(tongDon),
                firstOrder
        );
    }
}
