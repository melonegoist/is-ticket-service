export interface Coordinates {
  x: number;
  y: number;
}

export interface Location {
  x: number;
  y: number;
  z: number;
}

export interface Person {
  eyeColor: Color;
  hairColor?: Color;
  location?: Location;
  passportID: string;
  nationality: Country;
}

export interface Event {
  id: number;
  name: string;
  date: string;
  minAge?: number;
  ticketsCount: number;
}

export interface Address {
  street?: string;
}

export interface Venue {
  id: number;
  name: string;
  capacity: number;
  type: VenueType;
  address: Address;
}

export interface Ticket {
  id: number;
  name: string;
  coordinates: Coordinates;
  created: string;
  person: Person;
  event?: Event;
  price: number;
  ticketType?: TicketType;
  discount: number;
  number?: number;
  venue: Venue;
  createdBy?: string;
  createdAt?: string;
  updatedAt?: string;
}

export enum TicketType {
  VIP = 'VIP',
  BUDGETARY = 'BUDGETARY',
  CHEAP = 'CHEAP'
}

export enum Color {
  GREEN = 'GREEN',
  BLACK = 'BLACK',
  YELLOW = 'YELLOW',
  BROWN = 'BROWN'
}

export enum Country {
  SPAIN = 'SPAIN',
  CHINA = 'CHINA',
  INDIA = 'INDIA',
  SOUTH_KOREA = 'SOUTH_KOREA',
  NORTH_KOREA = 'NORTH_KOREA'
}

export enum VenueType {
  PUB = 'PUB',
  OPEN_AREA = 'OPEN_AREA',
  CINEMA = 'CINEMA',
  STADIUM = 'STADIUM'
}
