package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    void insert(AddressBook addressBook);

    List<AddressBook> selectByCondition(AddressBook addressBook);

    void update(AddressBook addressBook);

    @Select("select * from address_book where id = #{id}")
    AddressBook selectById(Long id);

    @Delete("delete from address_book where id = #{id}")
    void deleteById(Long id);

    @Update("update address_book set is_default = #{isDefault} where user_id = #{userId}")
    void updateDefaultByUserId(AddressBook addressBook);
}
