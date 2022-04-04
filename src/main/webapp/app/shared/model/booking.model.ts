import { Moment } from 'moment';
import { IJob } from 'app/shared/model/job.model';
import { IGarage } from 'app/shared/model/garage.model';
import { IVehicle } from 'app/shared/model/vehicle.model';
import { ICustomer } from 'app/shared/model/customer.model';
import { IQuote } from 'app/shared/model/quote.model';
import { IInvoice } from 'app/shared/model/invoice.model';
import { BookingStatus } from 'app/shared/model/enumerations/booking-status.model';

export interface IBooking {
  id?: number;
  bookingDate?: Moment;
  bookingTime?: Moment;
  furtherInstruction?: string;
  status?: BookingStatus;
  jobs?: IJob[];
  garage?: IGarage;
  vehicle?: IVehicle;
  customer?: ICustomer;
  quote?: IQuote;
  invoice?: IInvoice;
  searchTag?: string;
  mileage?: number;
  reference?: string;
}

export class Booking implements IBooking {
  constructor(
    public id?: number,
    public bookingDate?: Moment,
    public bookingTime?: Moment,
    public furtherInstruction?: string,
    public status?: BookingStatus,
    public jobs?: IJob[],
    public garage?: IGarage,
    public vehicle?: IVehicle,
    public customer?: ICustomer,
    public quote?: IQuote,
    public invoice?: IInvoice,
    public searchTag?: string,
    public mileage?: number,
    public reference?: string
  ) {}
}
