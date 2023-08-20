package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Override
    public void updateStatusById(Long id, Integer status) {
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmealMapper.update(setmeal);
    }

    @Override
    public void update(SetmealDTO setmealDTO) {
        // update Setmeal
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);

        // delete old SetmealDish
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());

        // insert new SetmealDish
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        if (!(setmealDishList != null && setmealDishList.size() > 0)) {
            return;
        }
        for (SetmealDish setmealDish : setmealDishList) {
            setmealDish.setSetmealId(setmealDTO.getId());
        }
        setmealDishMapper.insertList(setmealDishList);
    }

    @Override
    public SetmealVO selectById(Long setmealId) {
        Setmeal setmeal = setmealMapper.selectById(setmealId);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        List<SetmealDish> setmealDishList = setmealDishMapper.selectBySetmealId(setmealId);
        setmealVO.setSetmealDishes(setmealDishList);
        return setmealVO;
    }

    @Override
    @Transactional
    public void deleteByIdList(List<Long> idList) {
        // can not delete Setmeal that on sale
        for (Long id : idList) {
            if (setmealMapper.selectStatusById(id).equals(StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        // delete SetmealDish related by Setmeal
        setmealDishMapper.deleteByIdList(idList);

        // delete Setmeal
        setmealMapper.deleteByIdList(idList);
    }

    @Override
    public PageResult selectByPage(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<Setmeal> page = (Page<Setmeal>) setmealMapper.selectByPage(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void insert(SetmealDTO setmealDTO) {
        // insert Setmeal
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);

        // insert SetmealDish
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        setmealDishMapper.insertList(setmealDishList);
    }
}
