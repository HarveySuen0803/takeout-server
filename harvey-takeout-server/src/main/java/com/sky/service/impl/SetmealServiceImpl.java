package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;
    @Autowired
    DishMapper dishMapper;

    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    @Override
    public void updateStatusById(Long id, Integer status) {
        // can not enable Setmeal that Dish is not on sale
        if (status.equals(StatusConstant.ENABLE)) {
            List<Dish> dishList = dishMapper.selectBySetmealId(id);
            for (Dish dish : dishList) {
                if (dish.getStatus().equals(StatusConstant.ENABLE)) {
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }

        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmealMapper.update(setmeal);
    }

    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
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

    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
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
        Page<SetmealVO> page = (Page<SetmealVO>) setmealMapper.selectByPage(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.categoryId")
    @Override
    public void insert(SetmealDTO setmealDTO) {
        // insert Setmeal
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);

        // insert SetmealDish
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishList) {
            setmealDish.setSetmealId(setmeal.getId());
        }
        setmealDishMapper.insertList(setmealDishList);
    }

    @Cacheable(cacheNames = "setmealCache", key = "#categoryId")
    @Override
    public List<Setmeal> selectByCategoryId(Long categoryId) {
        Setmeal setmeal = new Setmeal();
        setmeal.setCategoryId(categoryId);
        setmeal.setStatus(StatusConstant.ENABLE);
        return setmealMapper.selectByCondition(setmeal);
    }

    @Override
    public List<DishItemVO> selectSetmealDishById(Long id) {
        return setmealDishMapper.selectWithImageAndDescriptionBySetmealId(id);
    }
}
