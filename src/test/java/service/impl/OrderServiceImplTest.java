package service.impl;

import model.CustomerOrder;
import model.Order;
import model.OrderItem;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.OrderDAO;
import util.Menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderServiceImplTest {

    @Mock
    private OrderDAO orderDAOMock;

    @Mock
    private Menu menuMock;

    @InjectMocks
    private OrderServiceImpl orderService;

    private String customer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        customer = "Yegor";
    }

    @Test
    public void addNewOrder() {
        // Given
        when(orderDAOMock.getCurrentOrder()).thenReturn(null);

        String productItem = "product";
        when(menuMock.getPrice(productItem)).thenReturn(99.9);

        // When
        orderService.addOrder(customer, productItem);

        // Then
        verify(orderDAOMock).saveOrder(isA(Order.class));
    }

    @Test
    public void repeatRecentCustomerOrder() {
        // Given
        Order recentOrder = new Order();

        CustomerOrder customerOrder = createCustomerOrder(customer, new OrderItem("chair", 99.9));
        recentOrder.addCustomerOrder(customerOrder);

        CustomerOrder otherCustomerOrder = createCustomerOrder("Petr", new OrderItem("car", 222.0));
        recentOrder.addCustomerOrder(otherCustomerOrder);

        when(orderDAOMock.getRecentOrder(customer)).thenReturn(recentOrder);

        Order currentOrder = new Order();
        when(orderDAOMock.getCurrentOrder()).thenReturn(currentOrder);

        // When
        boolean wasAdded = orderService.repeatOrder(customer);

        // Then
        Order expectedOrder = new Order();
        expectedOrder.addCustomerOrder(customerOrder);
        verify(orderDAOMock).saveOrder(eq(expectedOrder));

        assertTrue(wasAdded);
    }

    private CustomerOrder createCustomerOrder(String customer, OrderItem... orderitems) {
        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setName(customer);
        customerOrder.setOrders(new ArrayList<>(asList(orderitems)));
        return customerOrder;
    }

    @Test
    public void recentCustomerOrderIsAbsent() {
        // Given
        when(orderDAOMock.getRecentOrder(customer)).thenReturn(null);

        // When
        boolean wasAdded = orderService.repeatOrder(customer);

        // Then
        verify(orderDAOMock, never()).saveOrder(any(Order.class));

        assertFalse(wasAdded);
    }

}
