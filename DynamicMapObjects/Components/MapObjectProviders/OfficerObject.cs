using Genetec.Sdk;
using System.Collections.Generic;
using DynamicMapObjects.Maps;

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

    class OfficerObject
    {
        #region Constants

        private readonly OfficerMapObject m_officer;

        #endregion

        #region Fields
        public string ID;
        #endregion

        #region Properties

        public OfficerMapObject Officer
        {
            get { return m_officer; }
        }

        #endregion

        #region Constructors

        public OfficerObject(OfficerMapObject officer, GeoCoordinate initialPos)
        {
            m_officer = officer;
            m_officer.Latitude = initialPos.Latitude;
            m_officer.Longitude = initialPos.Longitude;
        }

        #endregion

        #region Public Methods

        public void Update()
        {
            
        }

        #endregion
    }

    #endregion
}

