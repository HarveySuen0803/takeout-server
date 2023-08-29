package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    AddressBookMapper addressBookMapper;

    @Override
    public void insert(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    @Override
    public AddressBook selectById(Long id) {
        return addressBookMapper.selectById(id);
    }

    @Override
    public List<AddressBook> selectByUserId() {
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        return addressBookMapper.selectByCondition(addressBook);
    }

    @Override
    public AddressBook selectDefaultByUserId() {
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(1);

        List<AddressBook> addressBookList = addressBookMapper.selectByCondition(addressBook);

        if (addressBookList == null || addressBookList.size() == 0) {
            return null;
        }

        return addressBookList.get(0);
    }

    @Override
    public void updateById(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    @Override
    public void updateDefaultById(Long id) {
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.updateDefaultByUserId(addressBook);

        addressBook.setId(id);
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);
    }

    @Override
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }
}
