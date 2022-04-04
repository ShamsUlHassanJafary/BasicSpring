import { Moment } from 'moment';
import { IBooking } from 'app/shared/model/booking.model';

export interface IQuote {
  id?: number;
  quoteDate?: Moment;
  quoteTotal?: number;
  discount?: number;
  booking?: IBooking;
  reference?: string;
}

export class Quote implements IQuote {
  constructor(
    public id?: number,
    public quoteDate?: Moment,
    public quoteTotal?: number,
    public discount?: number,
    public booking?: IBooking,
    public reference?: string
  ) {}
}
