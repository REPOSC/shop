package cn.alumik.shop.service;

import cn.alumik.shop.dao.CartRepository;
import cn.alumik.shop.entity.Cart;
import cn.alumik.shop.entity.Item;
import cn.alumik.shop.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Service
public class CartServiceImpl implements CartService {

    private CartRepository cartRepository;
    private SecurityService securityService;
    private UserService userService;

    public CartServiceImpl(CartRepository cartRepository, SecurityService securityService, UserService userService) {
        this.cartRepository = cartRepository;
        this.securityService = securityService;
        this.userService = userService;
    }

    @Override
    public Set<Cart> findAll() {
        String username = securityService.findLoggedInUsername();
        User user = userService.findByUsername(username);
        return user.getCarts();
    }

    @Override
    public void delete(int id) {
        cartRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        cartRepository.deleteAll();
        cartRepository.flush();
    }

    @Override
    public void save(Cart cart) {
        cart.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        cartRepository.save(cart);
    }

    @Override
    public Integer findExistUserItem(Item item) {
        User user = userService.findByUsername(securityService.findLoggedInUsername());
        Cart cart = cartRepository.findByUserAndItem(user, item);
        if (cart == null){
            return null;
        }
        else {
            return cart.getId();
        }
    }

    @Override
    public Cart getById(int id) {
        return cartRepository.findById(id).get();
    }

    @Override
    public List<Cart> findAllByUser_UsernameOrderByCreatedAt(String username) {
        return cartRepository.findAllByUser_UsernameOrderByCreatedAt(username);
    }

    @Override
    @Transactional
    public void deleteByUser_Username(String username) {
        cartRepository.deleteByUser_Username(username);
    }
}
