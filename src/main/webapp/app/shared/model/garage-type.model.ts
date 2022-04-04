export interface IGarageType {
  id?: number;
  name?: string;
}

export class GarageType implements IGarageType {
  constructor(public id?: number, public name?: string) {}
}
