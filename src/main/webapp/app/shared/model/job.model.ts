import { Moment } from 'moment';
import { IGarage } from 'app/shared/model/garage.model';
import { IBooking } from 'app/shared/model/booking.model';

export interface IJob {
  id?: number;
  description?: string;
  dateCreated?: Moment;
  price?: number;
  garage?: IGarage;
  bookings?: IBooking[];
  disabled?: boolean;
  reference?: string;
}

export class Job implements IJob {
  constructor(
    public id?: number,
    public description?: string,
    public dateCreated?: Moment,
    public price?: number,
    public garage?: IGarage,
    public bookings?: IBooking[],
    public disabled?: boolean,
    public reference?: string
  ) {}
}
