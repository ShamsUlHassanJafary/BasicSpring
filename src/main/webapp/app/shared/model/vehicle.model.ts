import { Moment } from 'moment';
import { IBooking } from 'app/shared/model/booking.model';
import { IGarage } from 'app/shared/model/garage.model';
import { ICustomer } from 'app/shared/model/customer.model';

export interface IVehicle {
  id?: number;
  registration?: string;
  make?: string;
  model?: string;
  colour?: string;
  motExpiryDate?: Moment;
  bookings?: IBooking[];
  garages?: IGarage[];
  owners?: ICustomer[];
  vehicleToString?: string;
}

export class Vehicle implements IVehicle {
  constructor(
    public id?: number,
    public registration?: string,
    public make?: string,
    public model?: string,
    public colour?: string,
    public motExpiryDate?: Moment,
    public bookings?: IBooking[],
    public garages?: IGarage[],
    public owners?: ICustomer[],
    public vehicleToString?: string
  ) {}
}
