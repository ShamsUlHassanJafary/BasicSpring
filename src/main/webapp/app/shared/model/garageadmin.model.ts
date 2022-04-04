import { IUser } from 'app/core/user/user.model';

export interface IGarageadmin {
  id?: number;
  user?: IUser;
}

export class Garageadmin implements IGarageadmin {
  constructor(public id?: number, public user?: IUser) {}
}
