package com.minise.leave.domain;

/**
 * 3 vai tro co dinh trong he thong. Chi 3 gia tri, gan nhu khong doi theo thoi gian
 * nen luu thang dang string tren cot `role`, khong can bang lookup rieng (roles table).
 * Neu sau nay can gan nhieu role/thay doi linh hoat qua UI admin, do la luc tach bang.
 */
public enum Role {
    EMPLOYEE,
    MANAGER,
    HR
}
