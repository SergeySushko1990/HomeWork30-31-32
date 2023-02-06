package ru.learnUp.learnupjava23.controller;

import org.springframework.web.bind.annotation.*;
import ru.learnUp.learnupjava23.dao.entity.OrderDetails;
import ru.learnUp.learnupjava23.dao.service.BookService;
import ru.learnUp.learnupjava23.dao.service.BooksOrderService;
import ru.learnUp.learnupjava23.dao.service.OrderDetailsService;
import ru.learnUp.learnupjava23.view.OrderDetailsFromView;
import ru.learnUp.learnupjava23.view.OrderDetailsToView;

import javax.persistence.EntityExistsException;

@RestController
@RequestMapping("rest/order_details")
public class OrderDetailsControllerRest {

    public final OrderDetailsService detailsService;
    public final OrderDetailsToView mapper;
    public final OrderDetailsFromView mapperFrom;
    public final BookService bookService;
    public final BooksOrderService orderService;

    public OrderDetailsControllerRest(OrderDetailsService detailsService, OrderDetailsToView mapper,
                                      OrderDetailsFromView mapperFrom, BookService bookService,
                                      BooksOrderService orderService) {
        this.detailsService = detailsService;
        this.mapper = mapper;
        this.mapperFrom = mapperFrom;
        this.bookService = bookService;
        this.orderService = orderService;
    }

    @GetMapping("/{order_detailsId}")
    public OrderDetailsToView getDetails(@PathVariable("order_detailsId") Long order_detailsId) {
        return mapper.mapToView(detailsService.getOrderDetailById(order_detailsId));
    }

    @PostMapping
    public OrderDetailsToView createOrderDetail(@RequestBody OrderDetailsFromView body) {
        if (body.getId() != null) {
            throw new EntityExistsException("Id must be null");
        }
        OrderDetails orderDetails = mapperFrom.mapFromView(body, orderService, bookService);
        OrderDetails createdOrderDetails = detailsService.createOrderDetails(orderDetails);
        return mapper.mapToView(createdOrderDetails);
    }


    @DeleteMapping("/{order_detailsId}")
    public Boolean deleteOrderDetail(@PathVariable("order_detailsId") Long id) {
        return detailsService.delete(id);
    }
}
