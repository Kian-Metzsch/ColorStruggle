package com.nordicnerds.colorstruggle;

class validate
{
    int validateScore(int color_int, float newValX, boolean restoreShieldChargesValue)
    {
        int color_int_reutrn = 0;
        switch (color_int)
        {
            case 0: // splashsprite_2d34c6_drop - blue
                if (newValX >= 139.0f & newValX <= 177.0f)
                    color_int_reutrn = 0;
                else if (restoreShieldChargesValue)
                    color_int_reutrn = 1;
                else
                    color_int_reutrn = 2;
                return color_int_reutrn;
            case 1: // splashsprite_2dbec6_drop - cyan
                if (newValX >= 2.0f & newValX <= 43.0f)
                    color_int_reutrn = 0;
                else if (restoreShieldChargesValue)
                    color_int_reutrn = 1;
                else
                    color_int_reutrn = 2;
                return color_int_reutrn;
            case 2: // splashsprite_2dc63c_drop - green
                if (newValX >= 229.0f & newValX <= 266)
                    color_int_reutrn = 0;
                else if (restoreShieldChargesValue)
                    color_int_reutrn = 1;
                else
                    color_int_reutrn = 2;
                return color_int_reutrn;
            case 3: // splashsprite_800080_drop - purple
                if (newValX >= 49.0f & newValX <= 87.0f)
                    color_int_reutrn = 0;
                else if (restoreShieldChargesValue)
                    color_int_reutrn = 1;
                else
                    color_int_reutrn = 2;
                return color_int_reutrn;
            case 4: // splashsprite_e1e100_drop - yellow
                if (newValX >= 183.0f & newValX <= 222.0f)
                    color_int_reutrn = 0;
                else if (restoreShieldChargesValue)
                    color_int_reutrn = 1;
                else
                    color_int_reutrn = 2;
                return color_int_reutrn;
            case 5: // splashsprite_ff00f0_drop - pink
                if (newValX >= 273.0f & newValX <= 312.0f)
                    color_int_reutrn = 0;
                else if (restoreShieldChargesValue)
                    color_int_reutrn = 1;
                else
                    color_int_reutrn = 2;
                return color_int_reutrn;
            case 6: // splashsprite_ff0000_drop - red
                if (newValX >= 94.0f & newValX <= 132.0f)
                    color_int_reutrn = 0;
                else if (restoreShieldChargesValue)
                    color_int_reutrn = 1;
                else
                    color_int_reutrn = 2;
                return color_int_reutrn;
            case 7: // splashsprite_ff8000_drop - orange
                if (newValX >= 319.0f & newValX <= 357.0f)
                    color_int_reutrn = 0;
                else if (restoreShieldChargesValue)
                    color_int_reutrn = 1;
                else
                    color_int_reutrn = 2;
                return color_int_reutrn;
        }
        return color_int_reutrn;
    }
}