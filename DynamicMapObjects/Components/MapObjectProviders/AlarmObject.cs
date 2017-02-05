using Genetec.Sdk;
using System.Collections.Generic;
using DynamicMapObjects.Maps;
using Genetec.Sdk.Entities.Maps;

// ==========================================================================
// Copyright (C) 2015 by Genetec, Inc.
// All rights reserved.
// May be used only in accordance with a valid Source Code License Agreement.
//
// Ephemerides for October 22:
//  1924 – Toastmasters International is founded.
//  1975 – The Soviet unmanned space mission Venera 9 lands on Venus.
//  2008 – India launches its first unmanned lunar mission Chandrayaan-1.
// ==========================================================================
namespace DynamicMapObjects.Components.MapObjectProviders
{
    #region Classes

    class AlarmObject
    {
        #region Constants

        private readonly AlarmCustomMapObject m_alarmObject;

        #endregion

        #region Fields
        public string ID;
        #endregion

        #region Properties

        public AlarmCustomMapObject AlarmMapObj
        {
            get { return m_alarmObject; }
        }

        #endregion

        #region Constructors

        public AlarmObject(AlarmCustomMapObject alarm, GeoCoordinate initialPos)
        {
            m_alarmObject = alarm;
            m_alarmObject.Latitude = initialPos.Latitude;
            m_alarmObject.Longitude = initialPos.Longitude;
        }

        #endregion

        #region Public Methods
        

        #endregion
    }

    #endregion
}

