package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    void insert(AddressBook addressBook);

    List<AddressBook> selectByUserId();

    AddressBook selectDefaultByUserId();

    void updateById(AddressBook addressBook);

    AddressBook selectById(Long id);

    void deleteById(Long id);

    void updateDefaultById(AddressBook addressBook);
}
