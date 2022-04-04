import { Moment } from 'moment';
import { IBooking } from 'app/shared/model/booking.model';

export interface IInvoice {
  id?: number;
  invoiceDate?: Moment;
  issueDate?: Moment;
  invoiceTotal?: number;
  paid?: number;
  balance?: number;
  discount?: number;
  booking?: IBooking;
  reference?: string;
}

export class Invoice implements IInvoice {
  constructor(
    public id?: number,
    public invoiceDate?: Moment,
    public issueDate?: Moment,
    public invoiceTotal?: number,
    public paid?: number,
    public balance?: number,
    public discount?: number,
    public booking?: IBooking,
    public reference?: string
  ) {}
}
