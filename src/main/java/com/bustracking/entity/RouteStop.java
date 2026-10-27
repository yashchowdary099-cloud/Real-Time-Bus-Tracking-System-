package com.bustracking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "route_stops", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"route_id", "stop_order"})
})
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stop_id", nullable = false)
    private Stop stop;

    @Column(nullable = false)
    private int stopOrder;

    private Double distanceFromStartKm;

    private Integer scheduledMinutesFromStart;

    public RouteStop() {}

    public RouteStop(Route route, Stop stop, int stopOrder, Double distanceFromStartKm, Integer scheduledMinutesFromStart) {
        this.route = route;
        this.stop = stop;
        this.stopOrder = stopOrder;
        this.distanceFromStartKm = distanceFromStartKm;
        this.scheduledMinutesFromStart = scheduledMinutesFromStart;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(int stopOrder) {
        this.stopOrder = stopOrder;
    }

    public Double getDistanceFromStartKm() {
        return distanceFromStartKm;
    }

    public void setDistanceFromStartKm(Double distanceFromStartKm) {
        this.distanceFromStartKm = distanceFromStartKm;
    }

    public Integer getScheduledMinutesFromStart() {
        return scheduledMinutesFromStart;
    }

    public void setScheduledMinutesFromStart(Integer scheduledMinutesFromStart) {
        this.scheduledMinutesFromStart = scheduledMinutesFromStart;
    }
}
