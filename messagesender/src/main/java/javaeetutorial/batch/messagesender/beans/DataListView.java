package javaeetutorial.batch.messagesender.beans;

import lombok.Data;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.io.Serializable;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

@ManagedBean
@ViewScoped
public class DataListView implements Serializable {
    private MessagesDataModel messagesDataModel;
    private CarView selectedCar;

    @EJB
    BatchMessageSender batchMessageSender;

    @ManagedProperty("#{carService}")
    private CarService service;

    @PostConstruct
    public void init() {
        messagesDataModel = new MessagesDataModel();
    }

    public MessagesDataModel getCars() {
        return messagesDataModel;
    }

    public void setService(CarService service) {
        this.service = service;
    }

    public CarView getSelectedCar() {
        return selectedCar;
    }

    public void setSelectedCar(CarView selectedCar) {
        this.selectedCar = selectedCar;
    }

    public void addJob(CarView carView) {
        batchMessageSender.registerMessage(carView.getId(), carView.getId());
    }

    @Data
    public class CarView {
        private final Long id;
        private String brand;
        private int year;
        private String state;

        private CarView(Car original, String state) {
            this.id = original.getId();
            this.brand = original.getBrand();
            this.year = original.getYear();
            this.state = state;
        }
    }

    public class MessagesDataModel extends LazyDataModel<CarView> {
        private static final long serialVersionUID = 1L;

        MessagesDataModel() {
            setRowCount(50);
        }

        @Override
        public List<CarView> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
            try {
                List<CarView> result = new ArrayList<>();
                List<Car> page = service.getPage(first, pageSize);
                for (Car car : page) {
                    result.add(new CarView(car, batchMessageSender.sendingStatus(car.getId())));
                }
                return result;
            } catch (Exception e) {
                setRowCount(0);
                return Collections.emptyList();
            }
        }
    }
}