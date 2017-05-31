package repository.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mongodb.morphia.Datastore;

public class OrderDAOImplTest {

    @Mock
    private Datastore datastoreMock;

    private OrderDAOImpl orderDAO = new OrderDAOImpl(datastoreMock);

    @Before
    public void globalSetup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddOrder() {
        // Given

        // Then

        // When
    }

}
